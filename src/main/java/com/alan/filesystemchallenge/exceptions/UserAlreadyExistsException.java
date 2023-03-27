package com.alan.filesystemchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends CustomException {
	public UserAlreadyExistsException() {
		super("Username already exists");
	}
}
