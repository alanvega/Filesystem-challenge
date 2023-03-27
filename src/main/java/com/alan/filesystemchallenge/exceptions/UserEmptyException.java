package com.alan.filesystemchallenge.exceptions;

public class UserEmptyException extends CustomException {
	public UserEmptyException() {
		super("Username or password cannot be empty");
	}
}
