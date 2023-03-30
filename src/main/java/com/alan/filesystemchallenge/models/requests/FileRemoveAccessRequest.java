package com.alan.filesystemchallenge.models.requests;

import java.io.Serializable;

public class FileRemoveAccessRequest implements Serializable {
	private String username;
	private Long fileId;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
}
