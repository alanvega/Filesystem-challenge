package com.alan.filesystemchallenge.models;

import org.springframework.http.HttpHeaders;

import java.io.Serializable;

public class FileDownload implements Serializable {

	private byte[] file;
	private HttpHeaders headers;

	public FileDownload(byte[] file, HttpHeaders headers) {
		this.file = file;
		this.headers = headers;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}
}
