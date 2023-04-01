package com.alan.filesystemchallenge.services;

import com.alan.filesystemchallenge.exceptions.FileEmptyException;
import com.alan.filesystemchallenge.exceptions.FileNameEmptyException;
import com.alan.filesystemchallenge.exceptions.FileNotFoundException;
import com.alan.filesystemchallenge.exceptions.UserCannotRemoveOwnAccessException;
import com.alan.filesystemchallenge.exceptions.UserDoesNotHaveAccessToFileException;
import com.alan.filesystemchallenge.exceptions.UserIsNotTheFileOwnerException;
import com.alan.filesystemchallenge.exceptions.UserNotAuthenticatedException;
import com.alan.filesystemchallenge.exceptions.UserNotFoundException;
import com.alan.filesystemchallenge.models.FileDownload;
import com.alan.filesystemchallenge.models.builders.FileBuilder;
import com.alan.filesystemchallenge.models.builders.FileMetadataBuilder;
import com.alan.filesystemchallenge.models.builders.FileShareBuilder;
import com.alan.filesystemchallenge.models.entities.File;
import com.alan.filesystemchallenge.models.entities.FileShare;
import com.alan.filesystemchallenge.models.requests.FileRemoveAccessRequest;
import com.alan.filesystemchallenge.models.requests.FileRenameRequest;
import com.alan.filesystemchallenge.models.requests.FileRequest;
import com.alan.filesystemchallenge.models.requests.FileShareRequest;
import com.alan.filesystemchallenge.models.responses.FileMetadataResponse;
import com.alan.filesystemchallenge.models.responses.FileResponse;
import com.alan.filesystemchallenge.repositories.FileShareRepository;
import com.alan.filesystemchallenge.repositories.FilesRepository;
import com.alan.filesystemchallenge.repositories.UsersRepository;
import com.alan.filesystemchallenge.transformers.FileTransformer;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileService.class);

	private final FilesRepository filesRepository;
	private final FileShareRepository fileShareRepository;
	private final FileTransformer fileTransformer;
	private final UsersRepository usersRepository;

	@Autowired
	public FileService(FilesRepository filesRepository, FileShareRepository fileShareRepository, FileTransformer fileTransformer,
	                   UsersRepository usersRepository) {
		this.filesRepository = filesRepository;
		this.fileShareRepository = fileShareRepository;
		this.fileTransformer = fileTransformer;
		this.usersRepository = usersRepository;
	}

	public FileResponse uploadFile(FileRequest fileRequest) {
		var optionalOfUploadedFile = Optional.ofNullable(fileRequest.getFile());
		if (optionalOfUploadedFile.isEmpty() || optionalOfUploadedFile.get().isEmpty()) {
			throw new FileEmptyException();
		}

		var userId = this.validateAuthAndGetUserId();

		var uploadedFile = optionalOfUploadedFile.get();

		var name = Optional.of(fileRequest.getName()).orElse(uploadedFile.getOriginalFilename());
		logger.info("Uploading file with name {}...", name);

		var file = FileBuilder.builder()
				.withName(name)
				.withFileContent(uploadedFile)
				.withCreateDate(new Date())
				.build();

		var savedFile = this.filesRepository.save(file);

		logger.info("Saving file share for owner...");
		// default file share for owner
		var fileShare = FileShareBuilder.builder()
				.withUserId(userId)
				.withIsOwner(true)
				.withFileId(savedFile.getId())
				.build();

		fileShareRepository.save(fileShare);

		// On a real project maybe it wouldn't be a good idea to log the file id and the user id
		logger.info("File uploaded with id {}, name {}, and create date {} for user id {}",
				file.getId(),
				file.getName(),
				file.getCreateDate(),
				userId
		);

		return this.fileTransformer.transformFileResponse(savedFile);
	}

	public void shareFile(FileShareRequest fileShareRequest) {
		logger.info("Sharing file with id {} to username {}",
				fileShareRequest.getFileId(),
				fileShareRequest.getUsernameToShare());

		var userId = this.validateAuthAndGetUserId();

		var file = this.validateAndGetFile(fileShareRequest.getFileId());

		// I'm assuming that only the owner can share the file
		this.validateIfUserIsFileOwner(file.getFileShareInfo(), userId);

		logger.info("User is the file owner, finding if user to share exists...");
		var userToShare = this.usersRepository
				.findByUsername(fileShareRequest.getUsernameToShare())
				.orElseThrow(UserNotFoundException::new);

		logger.info("User to share exists, sharing file to user...");
		var fileShare = FileShareBuilder.builder()
				.withUserId(userToShare.getId())
				.withFileId(fileShareRequest.getFileId())
				.withIsOwner(false)
				.build();

		this.fileShareRepository.save(fileShare);
		logger.info("Saved file share...");
	}

	public List<FileMetadataResponse> getAllFiles() {
		var userId = this.validateAuthAndGetUserId();

		logger.info("Getting all files for user id {}...", userId);
		return this.fileShareRepository
				.findByUserId(userId)
				.stream()
				.map(fileShare -> FileMetadataBuilder.builder()
						.setName(fileShare.getFile().getName())
						.setFileId(fileShare.getFileId())
						.setIsOwner(fileShare.isOwner())
						.setCreatedDate(fileShare.getFile().getCreateDate())
						.build()
				).toList();
	}

	public FileDownload downloadFile(Long fileId) {
		var userId = this.validateAuthAndGetUserId();

		var file = validateAndGetFile(fileId);

		logger.info("Checking if user has access to the file...");
		file.getFileShareInfo()
				.stream()
				.filter(fileShare -> fileShare.getUserId().equals(userId))
				.findAny()
				.orElseThrow(UserDoesNotHaveAccessToFileException::new);

		logger.info("Downloading file {} for user id {}...", fileId, userId);
		return new FileDownload(file.getFileContent(), getHttpHeaders(file));
	}

	@Transactional
	public void deleteFile(Long fileId) {
		var userId = this.validateAuthAndGetUserId();

		this.validateAndGetFile(fileId);

		logger.info("File exists, checking if user is the file owner...");
		// I'm assuming that only the owner can delete the file
		this.validateIfUserIsFileOwner(this.fileShareRepository.findByFileId(fileId), userId);

		logger.info("User is the file owner, first deleting all file shares...");
		this.fileShareRepository.deleteByFileId(fileId);

		logger.info("Deleted all file shares, now deleting file...");
		this.filesRepository.deleteById(fileId);

		logger.info("Deleted file.");
	}

	public void removeAccess(FileRemoveAccessRequest fileRemoveAccessRequest) {
		var userId = this.validateAuthAndGetUserId();

		logger.info("Finding if shared user exists...");
		var sharedUser = this.usersRepository.findByUsername(fileRemoveAccessRequest.getUsername())
				.orElseThrow(UserNotFoundException::new);

		if(sharedUser.getId().equals(userId)) throw new UserCannotRemoveOwnAccessException();

		logger.info("Shared user exists, finding if there is a shared access to given file...");
		var fileShares = this.fileShareRepository.findByFileId(fileRemoveAccessRequest.getFileId());
		if(fileShares.isEmpty()) throw new FileNotFoundException();

		// I am assuming that only the owner can remove access to the file
		logger.info("There is a shared access to given file, checking if user is the file owner...");
		this.validateIfUserIsFileOwner(fileShares, userId);

		logger.info("User is the file owner, revoking file access...");
		var fileShareId = fileShares
				.stream()
				.filter(fileShare -> fileShare.getUserId().equals(sharedUser.getId()))
				.findFirst()
				.orElseThrow(UserDoesNotHaveAccessToFileException::new)
				.getId();

		this.fileShareRepository.deleteById(fileShareId);
		logger.info("Revoked file access with file id {} for username {}.",
				fileRemoveAccessRequest.getFileId(),
				fileRemoveAccessRequest.getUsername());
	}

	public void renameFile(FileRenameRequest fileRenameRequest) {
		if(!StringUtils.hasText(fileRenameRequest.getNewName())) throw new FileNameEmptyException();

		var userId = this.validateAuthAndGetUserId();

		var file = this.validateAndGetFile(fileRenameRequest.getFileId());

		// I'm assuming that only the owner can rename the file
		this.validateIfUserIsFileOwner(file.getFileShareInfo(), userId);

		logger.info("User is the file owner, renaming file...");
		filesRepository.updateName(fileRenameRequest.getNewName(), fileRenameRequest.getFileId());

		logger.info("File with id {}, and name {} renamed to {}.",
				fileRenameRequest.getFileId(),
				file.getName(),
				fileRenameRequest.getNewName());
	}

	// Headers to allow downloading the file
	private HttpHeaders getHttpHeaders(File file) {
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDisposition(ContentDisposition.builder("attachment")
				.filename(file.getName())
				.build());
		headers.setContentLength(file.getFileContent().length);
		return headers;
	}

	private void validateIfUserIsFileOwner(List<FileShare> fileShareList, Long userId) {
		logger.info("Checking if user is the file owner...");
		fileShareList
				.stream()
				.filter(FileShare::isOwner)
				.filter(fileshare -> fileshare.getUserId().equals(userId))
				.findFirst()
				.orElseThrow(UserIsNotTheFileOwnerException::new);
	}

	private File validateAndGetFile(Long fileId) {
		logger.info("Finding if file exists...");
		return this.filesRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
	}

	// this auth maybe should be on a filter or interceptor and the user should be stored on the MDC (then get it here)
	private Long validateAuthAndGetUserId() {
		logger.info("Getting user authentication and validating if user exists...");
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			logger.info("User is authenticated, finding user and getting user id (if exists)");
			var username = authentication.getName();
			var user = this.usersRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

			// in a real project it is not a good idea to log the username with the id
			logger.info("User {} with id {} is authenticated", username, user.getId());
			return user.getId();
		} else {
			throw new UserNotAuthenticatedException();
		}
	}
}
