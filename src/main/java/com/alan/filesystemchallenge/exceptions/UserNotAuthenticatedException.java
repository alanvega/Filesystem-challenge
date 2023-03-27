package com.alan.filesystemchallenge.exceptions;

public class UserNotAuthenticatedException extends CustomException {
	public UserNotAuthenticatedException() {
		super("User is not authenticated");
	}
}
