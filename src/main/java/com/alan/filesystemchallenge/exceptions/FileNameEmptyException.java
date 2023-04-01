package com.alan.filesystemchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNameEmptyException extends CustomException {
	public FileNameEmptyException() {
		super("File name is empty");
	}
}
