package com.alan.filesystemchallenge.exceptions;

public class UserNotFoundException extends CustomException {
	public UserNotFoundException() {
		super("User not found");
	}
}
