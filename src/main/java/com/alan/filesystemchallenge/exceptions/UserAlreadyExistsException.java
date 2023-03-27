package com.alan.filesystemchallenge.exceptions;

public class UserAlreadyExistsException extends CustomException {
	public UserAlreadyExistsException() {
		super("Username already exists");
	}
}
