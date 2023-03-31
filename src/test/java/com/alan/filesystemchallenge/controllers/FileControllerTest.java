package com.alan.filesystemchallenge.controllers;

import com.alan.filesystemchallenge.models.FileDownload;
import com.alan.filesystemchallenge.models.requests.FileRemoveAccessRequest;
import com.alan.filesystemchallenge.models.requests.FileRenameRequest;
import com.alan.filesystemchallenge.models.requests.FileRequest;
import com.alan.filesystemchallenge.models.requests.FileShareRequest;
import com.alan.filesystemchallenge.models.responses.FileMetadataResponse;
import com.alan.filesystemchallenge.models.responses.FileResponse;
import com.alan.filesystemchallenge.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.testng.Assert.assertTrue;

class FileControllerTest {

	@Mock
	private final FileService mockFileService = Mockito.mock(FileService.class);

	private FileController fileController;

	@BeforeEach
	void setUp() {
		this.fileController = new FileController(mockFileService);
	}

	@Test
	void testUploadFile() {
		// Create a mock MultipartFile object with file data
		var file = new MockMultipartFile("file",
				"testfile.txt",
				"text/plain",
				"Hello, World!".getBytes());
		var fileRequest = new FileRequest();
		fileRequest.setFile(file);
		fileRequest.setName("testfile.txt");

		// Create a mock FileResponse object
		var expectedResponse = new FileResponse();
		expectedResponse.setName("testfile.txt");
		expectedResponse.setId(1L);
		expectedResponse.setCreateDate(new Date());

		Mockito.when(mockFileService.uploadFile(fileRequest)).thenReturn(expectedResponse);

		var actualResponse = this.fileController.upload(fileRequest);

		// Assert that the response matches the expected response
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void testShareFile() {
		var fileShareRequest = new FileShareRequest();
		fileShareRequest.setFileId(123L);
		fileShareRequest.setUsernameToShare("pepe");

		// Create a mock FileService and set its behavior
		Mockito.doNothing().when(mockFileService).shareFile(fileShareRequest);

		var actualResponse = this.fileController.share(fileShareRequest);

		// Assert that the response matches the expected response
		assertEquals(HttpStatusCode.valueOf(200), actualResponse.getStatusCode());
		assertNull(actualResponse.getBody());
	}

	@Test
	void getAll() {
		var expectedResponse = new FileMetadataResponse();
		expectedResponse.setName("testfile.txt");
		expectedResponse.setFileId(1L);
		expectedResponse.setCreatedDate(new Date());
		expectedResponse.setOwner(true);

		Mockito.when(mockFileService.getAllFiles()).thenReturn(List.of(expectedResponse));

		var actualResponse = this.fileController.getAll();

		// Assert that the response matches the expected response
		assertEquals(HttpStatusCode.valueOf(200), actualResponse.getStatusCode());
		assertEquals(expectedResponse, actualResponse.getBody().get(0));
		assertFalse(actualResponse.getBody().isEmpty());
		var fileMetadataResponse = actualResponse.getBody().get(0);
		assertEquals(expectedResponse.getFileId(), fileMetadataResponse.getFileId());
		assertEquals(expectedResponse.getName(), fileMetadataResponse.getName());
		assertEquals(expectedResponse.getCreatedDate(), fileMetadataResponse.getCreatedDate());
		assertTrue(fileMetadataResponse.isOwner());
	}

	@Test
	void download() {
		var file = new byte[]{1, 2, 3, 4, 5};
		var expectedResponse = new FileDownload(file, new HttpHeaders());

		var fileId = 1L;
		Mockito.when(mockFileService.downloadFile(fileId)).thenReturn(expectedResponse);

		var actualResponse = this.fileController.download(fileId);

		// Assert that the response matches the expected response
		assertEquals(HttpStatusCode.valueOf(200), actualResponse.getStatusCode());
		assertEquals(file.length, Objects.requireNonNull(actualResponse.getBody()).length);
	}

	@Test
	void delete() {
		var fileId = 1L;
		Mockito.doNothing().when(mockFileService).deleteFile(fileId);

		var actualResponse = this.fileController.delete(fileId);

		// Assert that the response matches the expected response
		assertEquals(HttpStatusCode.valueOf(200), actualResponse.getStatusCode());
		assertNull(actualResponse.getBody());
	}

	@Test
	void removeAccess() {
		var expectedResponse = new FileRemoveAccessRequest();
		expectedResponse.setFileId(1L);
		expectedResponse.setUsername("pepe");

		Mockito.doNothing().when(mockFileService).removeAccess(expectedResponse);

		var actualResponse = this.fileController.removeAccess(expectedResponse);

		// Assert that the response matches the expected response
		assertEquals(HttpStatusCode.valueOf(200), actualResponse.getStatusCode());
		assertNull(actualResponse.getBody());
	}

	@Test
	void rename() {
		var expectedResponse = new FileRenameRequest();
		expectedResponse.setFileId(1L);
		expectedResponse.setNewName("filename.txt");

		Mockito.doNothing().when(mockFileService).renameFile(expectedResponse);

		var actualResponse = this.fileController.rename(expectedResponse);

		// Assert that the response matches the expected response
		assertEquals(HttpStatusCode.valueOf(200), actualResponse.getStatusCode());
		assertNull(actualResponse.getBody());
	}
}
