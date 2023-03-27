package com.alan.filesystemchallenge.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	private final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@GetMapping("/home")
	public String home() {
		logger.info("Get to home page");
		return "home-page";
	}

}
