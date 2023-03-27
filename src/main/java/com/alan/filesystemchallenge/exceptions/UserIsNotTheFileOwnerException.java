package com.alan.filesystemchallenge.exceptions;

public class UserIsNotTheFileOwnerException extends CustomException {
	public UserIsNotTheFileOwnerException() {
		super("User is no the file owner, it cannot be shared");
	}
}
