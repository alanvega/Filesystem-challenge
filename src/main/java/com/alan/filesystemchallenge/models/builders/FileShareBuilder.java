package com.alan.filesystemchallenge.models.builders;

import com.alan.filesystemchallenge.models.entities.FileShare;

public class FileShareBuilder {

	private Integer userId;
	private Boolean isOwner;
	private Long fileId;

	public static FileShareBuilder builder() {
		return new FileShareBuilder();
	}

	public FileShareBuilder withUserId(Integer user) {
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

	public FileShare build() {
		var fileShare = new FileShare();
		fileShare.setUserId(this.userId);
		fileShare.setOwner(this.isOwner);
		fileShare.setFileId(this.fileId);
		return fileShare;
	}
}
