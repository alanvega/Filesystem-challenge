package com.alan.filesystemchallenge.controllers;

import com.alan.filesystemchallenge.models.requests.FileRequest;
import com.alan.filesystemchallenge.models.requests.FileShareRequest;
import com.alan.filesystemchallenge.models.responses.FileMetadataResponse;
import com.alan.filesystemchallenge.models.responses.FileResponse;
import com.alan.filesystemchallenge.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/file")
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	private final FileService fileService;

	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping("/upload")
	public FileResponse upload(@ModelAttribute FileRequest fileRequest) {
		logger.info("Post to upload file");
		return this.fileService.uploadFile(fileRequest);
	}

	@PostMapping("/share")
	public ResponseEntity<Void> share(@RequestBody FileShareRequest fileShareRequest) {
		logger.info("Post to share file");
		this.fileService.shareFile(fileShareRequest);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/get-all")
	public ResponseEntity<List<FileMetadataResponse>> getAll() {
		logger.info("Get all files");
		return ResponseEntity.ok(this.fileService.getAllFiles());
	}

	@GetMapping("/download/{fileId}")
	public ResponseEntity<byte[]> download(@PathVariable Optional<Long> fileId) {
		logger.info("Download file");
		var fileDownload = this.fileService.downloadFile(fileId);
		return new ResponseEntity<>(fileDownload.getFile(), fileDownload.getHeaders(), HttpStatus.OK);
	}

	@DeleteMapping("/delete/{fileId}")
	public ResponseEntity<Void> delete(@PathVariable Optional<Long> fileId) {
		logger.info("Delete file");
		this.fileService.deleteFile(fileId);
		return ResponseEntity.ok().build();
	}

}
