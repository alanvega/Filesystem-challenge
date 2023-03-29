package com.alan.filesystemchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDoesNotHaveAccessToFileException extends CustomException {
	public UserDoesNotHaveAccessToFileException() {
		super("User does not have access to file");
	}
}
