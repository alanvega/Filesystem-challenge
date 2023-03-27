package com.alan.filesystemchallenge.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomException extends RuntimeException {
	private static final Logger logger = LoggerFactory.getLogger(CustomException.class);

	public CustomException(String message) {
		super(message);
		logger.error(message);
	}
}
