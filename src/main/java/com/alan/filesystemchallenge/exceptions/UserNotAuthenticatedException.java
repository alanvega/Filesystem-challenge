package com.alan.filesystemchallenge.exceptions;

public class UserNotAuthenticatedException extends RuntimeException {
	public UserNotAuthenticatedException(String message) {
		super(message);
	}
}
