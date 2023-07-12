package com.alan.filesystemchallenge.models.builders;

import com.alan.filesystemchallenge.models.responses.FileMetadataResponse;

import java.util.Date;

public class FileMetadataBuilder {
	private Long fileId;
	private String name;
	private Date createdDate;
	private boolean isOwner;

	public static FileMetadataBuilder builder() {
		return new FileMetadataBuilder();
	}

	public FileMetadataBuilder setFileId(Long fileId) {
		this.fileId = fileId;
		return this;
	}

	public FileMetadataBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public FileMetadataBuilder setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public FileMetadataBuilder setIsOwner(boolean isOwner) {
		this.isOwner = isOwner;
		return this;
	}

	public FileMetadataResponse build() {
		var fileMetadataResponse = new FileMetadataResponse();
		fileMetadataResponse.setFileId(this.fileId);
		fileMetadataResponse.setName(this.name);
		fileMetadataResponse.setCreatedDate(this.createdDate);
		fileMetadataResponse.setOwner(this.isOwner);
		return fileMetadataResponse;
	}

}
