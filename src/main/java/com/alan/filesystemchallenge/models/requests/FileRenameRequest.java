package com.alan.filesystemchallenge.models.requests;

import java.io.Serializable;

public class FileRenameRequest implements Serializable {
	private Long fileId;
	private String newName;

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}
}
