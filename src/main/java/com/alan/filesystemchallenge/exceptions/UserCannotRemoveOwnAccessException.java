package com.alan.filesystemchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserCannotRemoveOwnAccessException extends CustomException {
	public UserCannotRemoveOwnAccessException() {
		super("User cannot remove own access from file");
	}
}
