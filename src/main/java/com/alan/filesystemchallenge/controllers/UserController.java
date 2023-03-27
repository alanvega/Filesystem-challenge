package com.alan.filesystemchallenge.controllers;

import com.alan.filesystemchallenge.models.requests.UserRequest;
import com.alan.filesystemchallenge.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/create")
	public ResponseEntity<Void> create(@RequestBody UserRequest userRequest) {
		logger.info("Post to create user");
		this.userService.create(userRequest);
		return ResponseEntity.ok().build();
	}

}
