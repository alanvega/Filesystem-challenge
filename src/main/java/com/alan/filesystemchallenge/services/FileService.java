package com.alan.filesystemchallenge.services;

import com.alan.filesystemchallenge.exceptions.UserNotAuthenticatedException;
import com.alan.filesystemchallenge.exceptions.UserNotFoundException;
import com.alan.filesystemchallenge.models.builders.FileBuilder;
import com.alan.filesystemchallenge.models.builders.FileShareBuilder;
import com.alan.filesystemchallenge.models.requests.FileRequest;
import com.alan.filesystemchallenge.models.responses.FileResponse;
import com.alan.filesystemchallenge.repositories.FileShareRepository;
import com.alan.filesystemchallenge.repositories.FilesRepository;
import com.alan.filesystemchallenge.repositories.UsersRepository;
import com.alan.filesystemchallenge.transformers.FileTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
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
			logger.warn("File is empty");
			return null;
		}

		// this auth should be on a filter or interceptor and the user should be stored on the MDC (then get it here)
		logger.info("Getting user authentication");
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			var username = authentication.getName();
			var user = this.usersRepository.findByUsername(username);

			if(user.isEmpty()) {
				var message = "User not found";
				logger.error(message);
				throw new UserNotFoundException(message);
			}

			logger.info("User id {} is authenticated", user.get().getId());

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
					.withUserId(user.get().getId())
					.withIsOwner(true)
					.withFileId(savedFile.getId())
					.build();

			fileShareRepository.save(fileShare);

			// On a real project maybe it wouldn't be a good idea to log the file id
			logger.info("File uploaded with id {}, name {}, and create date {} for user id {}",
					file.getId(),
					file.getName(),
					file.getCreateDate(),
					user.get().getId()
			);

			return this.fileTransformer.transformFileResponse(file);
		}
		var message = "User is not authenticated";
		logger.error(message);
		throw new UserNotAuthenticatedException(message);

	}
}
