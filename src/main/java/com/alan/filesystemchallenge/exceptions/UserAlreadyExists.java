package com.alan.filesystemchallenge.exceptions;

public class UserAlreadyExists extends RuntimeException {
	public UserAlreadyExists(String message) {
		super(message);
	}
}
