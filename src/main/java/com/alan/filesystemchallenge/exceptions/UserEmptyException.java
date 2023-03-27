package com.alan.filesystemchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserEmptyException extends CustomException {
	public UserEmptyException() {
		super("Username or password cannot be empty");
	}
}
