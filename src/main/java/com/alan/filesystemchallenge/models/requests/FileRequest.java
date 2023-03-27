package com.alan.filesystemchallenge.models.requests;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class FileRequest implements Serializable {
	private String name;
	private MultipartFile file;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
}
