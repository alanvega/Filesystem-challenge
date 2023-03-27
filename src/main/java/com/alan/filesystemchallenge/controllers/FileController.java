package com.alan.filesystemchallenge.controllers;

import com.alan.filesystemchallenge.models.requests.FileRequest;
import com.alan.filesystemchallenge.models.responses.FileResponse;
import com.alan.filesystemchallenge.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
