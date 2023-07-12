package com.alan.filesystemchallenge.services;

import com.alan.filesystemchallenge.exceptions.FileEmptyException;
import com.alan.filesystemchallenge.exceptions.FileNameEmptyException;
import com.alan.filesystemchallenge.exceptions.FileNotFoundException;
import com.alan.filesystemchallenge.exceptions.UserCannotRemoveOwnAccessException;
import com.alan.filesystemchallenge.exceptions.UserDoesNotHaveAccessToFileException;
import com.alan.filesystemchallenge.exceptions.UserIsNotTheFileOwnerException;
import com.alan.filesystemchallenge.exceptions.UserNotAuthenticatedException;
import com.alan.filesystemchallenge.exceptions.UserNotFoundException;
import com.alan.filesystemchallenge.models.builders.FileBuilder;
import com.alan.filesystemchallenge.models.builders.FileShareBuilder;
import com.alan.filesystemchallenge.models.builders.UserBuilder;
import com.alan.filesystemchallenge.models.entities.File;
import com.alan.filesystemchallenge.models.entities.FileShare;
import com.alan.filesystemchallenge.models.requests.FileRemoveAccessRequest;
import com.alan.filesystemchallenge.models.requests.FileRenameRequest;
import com.alan.filesystemchallenge.models.requests.FileRequest;
import com.alan.filesystemchallenge.models.requests.FileShareRequest;
import com.alan.filesystemchallenge.repositories.FileShareRepository;
import com.alan.filesystemchallenge.repositories.FilesRepository;
import com.alan.filesystemchallenge.repositories.UsersRepository;
import com.alan.filesystemchallenge.transformers.FileTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

class FileServiceTest {

	private FileService fileService;

	private FilesRepository filesRepository;
	private FileShareRepository fileShareRepository;
	private UsersRepository usersRepository;

	private static final long USER_ID = 1L;
	private static final long FILE_ID = 2L;
	private static final String USERNAME = "username";

	@BeforeEach
	void setUp() {
		this.filesRepository = mock(FilesRepository.class);
		this.fileShareRepository = mock(FileShareRepository.class);
		var fileTransformer = new FileTransformer();
		this.usersRepository = mock(UsersRepository.class);
		this.fileService = new FileService(filesRepository, fileShareRepository, fileTransformer, usersRepository);

		authenticateUser();
	}

	@Test
	void shouldUploadFileSuccessfully() {
		// Mock the file request
		var fileContent = "This is a test file".getBytes();
		var fileName = "test.txt";
		var multipartFile = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, fileContent);
		var fileRequest = new FileRequest();
		fileRequest.setName(fileName);
		fileRequest.setFile(multipartFile);

		// Mock the file entity
		var expectedDate = new Date();
		var savedFile = FileBuilder.builder()
				.withName(fileName)
				.withFileContent(fileContent)
				.withCreateDate(expectedDate)
				.withFileId(FILE_ID)
				.build();

		when(filesRepository.save(any(File.class))).thenReturn(savedFile);

		// Invoke the method
		var response = fileService.uploadFile(fileRequest);

		// Verify the response
		assertNotNull(response);
		assertEquals(response.getId(), FILE_ID);
		assertEquals(response.getName(), fileName);
		assertEquals(response.getCreateDate(), expectedDate);

		// Verify the repository calls
		verify(filesRepository).save(any());
		verify(fileShareRepository).save(any());
	}

	@Test
	void shouldThrowFileEmptyExceptionWhenFileIsEmpty() {
		var fileRequest = new FileRequest();
		fileRequest.setName("test.txt");

		assertThrows(FileEmptyException.class, () -> fileService.uploadFile(fileRequest));
	}

	@Test
	void shouldThrowUserNotAuthenticatedExceptionWhenUserIsNotAuthenticated() {
		var fileContent = "This is a test file".getBytes();
		var fileName = "test.txt";
		var multipartFile = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, fileContent);
		var fileRequest = new FileRequest();
		fileRequest.setName(fileName);
		fileRequest.setFile(multipartFile);

		var authentication = Mockito.mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(false);

		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		assertThrows(UserNotAuthenticatedException.class, () -> fileService.uploadFile(fileRequest));
	}

	@Test
	void shouldThrowUserNotFoundWhenUserIsNotFoundOnUsersRepository() {
		var fileContent = "This is a test file".getBytes();
		var fileName = "test.txt";
		var multipartFile = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, fileContent);
		var fileRequest = new FileRequest();
		fileRequest.setName(fileName);
		fileRequest.setFile(multipartFile);

		var authentication = Mockito.mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(true);
		var username = "pepe";

		when(authentication.getName()).thenReturn(username);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		when(usersRepository.findByUsername(any())).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> fileService.uploadFile(fileRequest));
	}

	@Test
	void shareFile() {
		var fileShareRequest = new FileShareRequest();
		var username2 = "user2";
		fileShareRequest.setUsernameToShare(username2);
		fileShareRequest.setFileId(FILE_ID);

		var user = UserBuilder.builder()
				.withUsername(USERNAME)
				.withUserId(USER_ID)
				.build();

		var user2 = UserBuilder.builder()
				.withUsername(username2)
				.build();

		var result = getResult(USER_ID, true);

		when(usersRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
		when(usersRepository.findByUsername(username2)).thenReturn(Optional.of(user2));
		when(filesRepository.findById(FILE_ID)).thenReturn(Optional.of(result.file()));

		fileService.shareFile(fileShareRequest);

		// verify
		verify(filesRepository, times(1)).findById(FILE_ID);
		verify(usersRepository, times(2)).findByUsername(anyString());
	}

	@Test
	void shareFileShouldThrowFileNotFoundException() {
		var fileShareRequest = new FileShareRequest();
		var username2 = "user2";
		fileShareRequest.setUsernameToShare(username2);
		fileShareRequest.setFileId(FILE_ID);

		when(filesRepository.findById(FILE_ID)).thenReturn(Optional.empty());

		assertThrows(FileNotFoundException.class, () -> fileService.shareFile(fileShareRequest));

		// verify
		verify(filesRepository, times(1)).findById(FILE_ID);
	}

	@Test
	void shareFileShouldThrowUserIsNotTheFileOwnerException() {
		var fileShareRequest = new FileShareRequest();
		var username1 = "user1";
		var username2 = "user2";
		fileShareRequest.setUsernameToShare(username2);
		fileShareRequest.setFileId(FILE_ID);

		var user = UserBuilder.builder()
				.withUsername(username1)
				.build();

		var fileShareInfo = FileShareBuilder.builder()
				.withFileId(FILE_ID)
				.withIsOwner(false)
				.withUserId(USER_ID)
				.build();

		var file = FileBuilder.builder()
				.withFileId(FILE_ID)
				.withFileShareInfo(List.of(fileShareInfo))
				.build();

		when(usersRepository.findByUsername(username1)).thenReturn(Optional.of(user));
		when(filesRepository.findById(FILE_ID)).thenReturn(Optional.of(file));

		assertThrows(UserIsNotTheFileOwnerException.class, () -> fileService.shareFile(fileShareRequest));

		// verify
		verify(filesRepository, times(1)).findById(FILE_ID);
		verify(usersRepository, times(1)).findByUsername(anyString());
	}

	@Test
	void shareFileShouldThrowUserNotFoundException() {
		var fileShareRequest = new FileShareRequest();
		var username1 = "user1";
		var username2 = "user2";
		fileShareRequest.setUsernameToShare(username2);
		fileShareRequest.setFileId(FILE_ID);

		var user = UserBuilder.builder()
				.withUsername(username1)
				.build();

		var fileShareInfo = FileShareBuilder.builder()
				.withFileId(FILE_ID)
				.withIsOwner(true)
				.withUserId(USER_ID)
				.build();

		var file = FileBuilder.builder()
				.withFileId(FILE_ID)
				.withFileShareInfo(List.of(fileShareInfo))
				.build();

		when(usersRepository.findByUsername(username1)).thenReturn(Optional.of(user));
		when(usersRepository.findByUsername(username2)).thenReturn(Optional.empty());
		when(filesRepository.findById(FILE_ID)).thenReturn(Optional.of(file));

		assertThrows(UserNotFoundException.class, () -> fileService.shareFile(fileShareRequest));

		// verify
		verify(filesRepository, times(1)).findById(FILE_ID);
		verify(usersRepository, times(2)).findByUsername(anyString());
	}

	@Test
	void getAllFiles() {
		var result = getResult(USER_ID, false);

		var fileShares = List.of(result.fileShare());

		when(fileShareRepository.findByUserId(USER_ID)).thenReturn(fileShares);

		var allFiles = fileService.getAllFiles();

		assertEquals(allFiles.size(), 1);
		var response = allFiles.get(0);
		assertEquals(response.getName(), result.file().getName());
		assertEquals(response.getFileId(), FILE_ID);
		assertTrue(response.isOwner());

		verify(fileShareRepository, times(1)).findByUserId(USER_ID);
	}

	@Test
	void getAllFilesEmpty() {
		when(fileShareRepository.findByUserId(USER_ID)).thenReturn(List.of());
		var allFiles = fileService.getAllFiles();
		assertEquals(allFiles.size(), 0);

		verify(fileShareRepository, times(1)).findByUserId(USER_ID);
	}

	@Test
	void downloadFile() {
		var result = getResult(USER_ID, true);

		when(filesRepository.findById(FILE_ID)).thenReturn(Optional.of(result.file()));

		var fileDownload = fileService.downloadFile(FILE_ID);

		assertNotNull(fileDownload);
		assertEquals(fileDownload.getFile(), result.file().getFileContent());

		verify(filesRepository, times(1)).findById(FILE_ID);

		// Assert for headers to download file
		var headers = fileDownload.getHeaders();
		assertNotNull(headers);
		assertTrue(headers.containsKey(HttpHeaders.CONTENT_TYPE));
		assertTrue(headers.containsKey(HttpHeaders.CONTENT_DISPOSITION));
		assertTrue(headers.containsKey(HttpHeaders.CONTENT_LENGTH));
		assertTrue(headers.containsKey(HttpHeaders.CONTENT_LENGTH));
		assertEquals(headers.getContentType(), MediaType.APPLICATION_OCTET_STREAM);
		assertEquals("attachment; filename=\"" + result.file().getName() + "\"", headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));
		assertEquals(result.file().getFileContent().length, headers.getContentLength());
	}

	@Test
	void downloadFileShouldThrowsUserDoesNotHaveAccessToFileException() {
		var result = getResult(2L, true);

		when(filesRepository.findById(FILE_ID)).thenReturn(Optional.of(result.file()));

		assertThrows(UserDoesNotHaveAccessToFileException.class, () -> fileService.downloadFile(FILE_ID));

		verify(filesRepository, times(1)).findById(FILE_ID);

	}

	@Test
	void deleteFile() {
		var result = getResult(USER_ID, true);

		var fileShares = List.of(result.fileShare());
		when(filesRepository.findById(FILE_ID)).thenReturn(Optional.of(result.file()));
		when(fileShareRepository.findByFileId(FILE_ID)).thenReturn(fileShares);

		fileService.deleteFile(FILE_ID);

		verify(fileShareRepository, times(1)).deleteByFileId(FILE_ID);
		verify(filesRepository, times(1)).findById(FILE_ID);
		verify(filesRepository, times(1)).deleteById(FILE_ID);
	}

	@Test
	void removeAccess() {
		var username2 = "user2";
		var userId2 = 2L;

		var fileRemoveAccessRequest = new FileRemoveAccessRequest();
		fileRemoveAccessRequest.setFileId(FILE_ID);
		fileRemoveAccessRequest.setUsername(username2);

		var result = getResult(USER_ID, true);

		var fileShareForUser2 = FileShareBuilder.builder()
				.withFileId(FILE_ID)
				.withUserId(userId2)
				.withIsOwner(false)
				.build();

		var fileShares = List.of(result.fileShare(), fileShareForUser2);

		var user = UserBuilder.builder()
				.withUsername(username2)
				.withUserId(userId2)
				.build();

		when(fileShareRepository.findByFileId(FILE_ID)).thenReturn(fileShares);
		when(usersRepository.findByUsername(username2)).thenReturn(Optional.of(user));

		fileService.removeAccess(fileRemoveAccessRequest);

		verify(usersRepository, times(1)).findByUsername(username2);
		verify(usersRepository, times(1)).findByUsername(USERNAME);
		verify(fileShareRepository, times(1)).findByFileId(FILE_ID);
	}

	@Test
	void removeAccessShouldThrowsUserCannotRemoveOwnAccessException() {
		var fileRemoveAccessRequest = new FileRemoveAccessRequest();
		fileRemoveAccessRequest.setFileId(FILE_ID);
		fileRemoveAccessRequest.setUsername(USERNAME);

		var result = getResult(USER_ID, true);
		var fileShares = List.of(result.fileShare());

		var user = UserBuilder.builder()
				.withUsername(USERNAME)
				.withUserId(USER_ID)
				.build();

		when(fileShareRepository.findByFileId(FILE_ID)).thenReturn(fileShares);
		when(usersRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

		assertThrows(UserCannotRemoveOwnAccessException.class, () -> fileService.removeAccess(fileRemoveAccessRequest));

		verify(usersRepository, times(2)).findByUsername(USERNAME);
		verify(fileShareRepository, times(0)).findByFileId(FILE_ID);
	}

	@Test
	void removeAccessShouldThrowsUserNotFoundException() {
		var username = "pepe";
		var fileRemoveAccessRequest = new FileRemoveAccessRequest();
		fileRemoveAccessRequest.setFileId(FILE_ID);
		fileRemoveAccessRequest.setUsername(username);

		var result = getResult(USER_ID, true);
		var fileShares = List.of(result.fileShare());

		when(fileShareRepository.findByFileId(FILE_ID)).thenReturn(fileShares);
		when(usersRepository.findByUsername(username)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> fileService.removeAccess(fileRemoveAccessRequest));

		verify(usersRepository, times(1)).findByUsername(username);
		verify(fileShareRepository, times(0)).findByFileId(FILE_ID);
	}

	@Test
	void renameFile() {
		var fileNewName = "newName.txt";
		var fileRenameRequest = new FileRenameRequest();
		fileRenameRequest.setNewName(fileNewName);
		fileRenameRequest.setFileId(FILE_ID);

		var result = getResult(USER_ID, true);

		when(filesRepository.findById(FILE_ID)).thenReturn(Optional.of(result.file()));

		fileService.renameFile(fileRenameRequest);

		verify(filesRepository, times(1)).updateName(fileNewName, FILE_ID);
	}

	@Test
	void renameFileWithEmptyNewName() {
		var fileRenameRequest = new FileRenameRequest();
		fileRenameRequest.setFileId(FILE_ID);

		assertThrows(FileNameEmptyException.class, () -> fileService.renameFile(fileRenameRequest));

		verify(filesRepository, times(0)).updateName(any(), any());
	}

	private void authenticateUser() {
		var authentication = Mockito.mock(Authentication.class);
		when(authentication.isAuthenticated()).thenReturn(true);

		when(authentication.getName()).thenReturn(USERNAME);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		var user = UserBuilder.builder()
				.withUsername(USERNAME)
				.withUserId(USER_ID)
				.build();
		when(usersRepository.findByUsername(any())).thenReturn(Optional.of(user));
	}

	private static Result getResult(Long userId, Boolean fileWithShareList) {
		var date = new Date();
		var fileName = "test.txt";
		var fileShareBuilder = FileShareBuilder.builder()
				.withUserId(userId)
				.withIsOwner(true)
				.withFileId(FILE_ID);

		var fileBuilder = FileBuilder.builder()
				.withName(fileName)
				.withCreateDate(date)
				.withFileId(FILE_ID)
				.withFileContent("test".getBytes());

		if(fileWithShareList) {
			fileBuilder.withFileShareInfo(List.of(fileShareBuilder.build()));
		}

		var file = fileBuilder.build();

		if(!fileWithShareList) {
			fileShareBuilder.withFile(file);
		}

		var fileShare = fileShareBuilder.build();

		return new Result(fileShare, file);
	}

	private record Result(FileShare fileShare, File file) {}
}
