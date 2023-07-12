package com.alan.filesystemchallenge.models.requests;

import java.io.Serializable;

public class FileShareRequest implements Serializable {
	private Long fileId;
	private String usernameToShare;

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getUsernameToShare() {
		return usernameToShare;
	}

	public void setUsernameToShare(String usernameToShare) {
		this.usernameToShare = usernameToShare;
	}
}
