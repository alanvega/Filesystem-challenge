package com.alan.filesystemchallenge.models.builders;

import com.alan.filesystemchallenge.models.entities.File;
import com.alan.filesystemchallenge.models.entities.FileShare;

public class FileShareBuilder {

	private Long userId;
	private Boolean isOwner;
	private Long fileId;
	private File file;

	public static FileShareBuilder builder() {
		return new FileShareBuilder();
	}

	public FileShareBuilder withUserId(Long user) {
		this.userId = user;
		return this;
	}

	public FileShareBuilder withIsOwner(Boolean isOwner) {
		this.isOwner = isOwner;
		return this;
	}

	public FileShareBuilder withFileId(Long fileId) {
		this.fileId = fileId;
		return this;
	}

	public FileShareBuilder withFile(File file) {
		this.file = file;
		return this;
	}

	public FileShare build() {
		var fileShare = new FileShare();
		fileShare.setUserId(this.userId);
		fileShare.setIsOwner(this.isOwner);
		fileShare.setFileId(this.fileId);
		fileShare.setFile(this.file);
		return fileShare;
	}
}
