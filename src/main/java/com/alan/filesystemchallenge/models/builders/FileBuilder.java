package com.alan.filesystemchallenge.models.builders;

import com.alan.filesystemchallenge.models.entities.File;
import com.alan.filesystemchallenge.models.entities.FileShare;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public class FileBuilder {

	private String name;
	private Long fileId;
	private MultipartFile fileContent;
	private byte[] fileContentAsBytes;
	private Date createDate;
	private List<FileShare> fileShareInfo;

	public static FileBuilder builder() {
		return new FileBuilder();
	}

	public FileBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public FileBuilder withCreateDate(Date createDate) {
		this.createDate = createDate;
		return this;
	}

	public FileBuilder withFileContent(MultipartFile fileContent) {
		this.fileContent = fileContent;
		return this;
	}

	public FileBuilder withFileContent(byte[] fileContent) {
		this.fileContentAsBytes = fileContent;
		return this;
	}

	public FileBuilder withFileId(Long id) {
		this.fileId = id;
		return this;
	}

	public FileBuilder withFileShareInfo(List<FileShare> fileShareInfo) {
		this.setFileShareInfo(fileShareInfo);
		return this;
	}

	public File build() {
		var file = new File();
		file.setName(this.name);
		file.setCreateDate(this.createDate);
		file.setId(this.fileId);
		if(this.fileContent != null) file.setFileContent(this.fileContent);
		if(this.fileContentAsBytes != null) file.setFileContent(this.fileContentAsBytes);
		file.setFileShareInfo(this.fileShareInfo);
		return file;
	}

	public void setFileShareInfo(List<FileShare> fileShareInfo) {
		this.fileShareInfo = fileShareInfo;
	}

	public List<FileShare> getFileShareInfo() {
		return fileShareInfo;
	}
}
