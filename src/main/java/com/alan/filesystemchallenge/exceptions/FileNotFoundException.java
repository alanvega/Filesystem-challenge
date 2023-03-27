package com.alan.filesystemchallenge.exceptions;

public class FileNotFoundException extends CustomException {
	public FileNotFoundException() {
		super("File not found");
	}
}
