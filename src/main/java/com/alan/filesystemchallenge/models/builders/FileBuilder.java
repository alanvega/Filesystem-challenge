package com.alan.filesystemchallenge.models.builders;

import com.alan.filesystemchallenge.models.entities.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class FileBuilder {

	private String name;
	private MultipartFile fileContent;
	private byte[] fileContentAsBytes;
	private Date createDate;

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

	public File build() {
		var file = new File();
		file.setName(this.name);
		file.setCreateDate(this.createDate);
		if(this.fileContent != null) file.setFileContent(this.fileContent);
		if(this.fileContentAsBytes != null) file.setFileContent(this.fileContentAsBytes);
		return file;
	}

}
