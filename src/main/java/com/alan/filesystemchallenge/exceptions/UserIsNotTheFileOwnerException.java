package com.alan.filesystemchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserIsNotTheFileOwnerException extends CustomException {
	public UserIsNotTheFileOwnerException() {
		super("User is no the file owner");
	}
}
