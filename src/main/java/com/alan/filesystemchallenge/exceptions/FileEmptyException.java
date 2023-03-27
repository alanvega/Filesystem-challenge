package com.alan.filesystemchallenge.exceptions;

public class FileEmptyException extends CustomException {
	public FileEmptyException() {
		super("File is empty");
	}
}
