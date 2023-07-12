package com.alan.filesystemchallenge.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
	private final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

	@RequestMapping("error")
	public String handleError() {
		logger.error("Error page");
		return "error-page";
	}
}
