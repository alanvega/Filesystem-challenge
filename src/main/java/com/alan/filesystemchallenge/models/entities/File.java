package com.alan.filesystemchallenge.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Entity(name = "files")
public class File {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;

	private String name;

	@Column(name="file_content")
	private byte[] fileContent;

	@Column(name="create_date")
	private Date createDate;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "file_id", insertable = false, nullable = false)
	private List<FileShare> fileShareInfo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(MultipartFile fileContent) {
		try {
			this.fileContent = fileContent.getBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public List<FileShare> getFileShareInfo() {
		return fileShareInfo;
	}

	public void setFileShareInfo(List<FileShare> fileShareInfo) {
		this.fileShareInfo = fileShareInfo;
	}
}
