package com.alan.filesystemchallenge.models.builders;

import com.alan.filesystemchallenge.models.entities.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class FileBuilder {

	private String name;
	private MultipartFile fileContent;
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

	public File build() {
		var file = new File();
		file.setName(this.name);
		file.setCreateDate(this.createDate);
		file.setFileContent(fileContent);
		return file;
	}

}
