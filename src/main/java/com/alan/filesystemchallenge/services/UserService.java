package com.alan.filesystemchallenge.services;

import com.alan.filesystemchallenge.exceptions.UserAlreadyExistsException;
import com.alan.filesystemchallenge.exceptions.UserEmptyException;
import com.alan.filesystemchallenge.models.builders.UserBuilder;
import com.alan.filesystemchallenge.models.requests.UserRequest;
import com.alan.filesystemchallenge.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UsersRepository usersRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
		this.usersRepository = usersRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void create(UserRequest userRequest) {
		// this validations should be moved to a validation service
		if(userRequest.getUsername().isEmpty() || userRequest.getPassword().isEmpty()) {
			throw new UserEmptyException();
		}

		// in a real project, this would be better not to notify the user that the username already exists
		if(this.usersRepository.findByUsername(userRequest.getUsername()).isPresent()) {
			throw new UserAlreadyExistsException();
		}

		logger.info("Creating user with username {}...", userRequest.getUsername());
		var user = UserBuilder.builder()
				.setUsername(userRequest.getUsername())
				.setPassword(this.passwordEncoder.encode(userRequest.getPassword()))
				.build();


		this.usersRepository.save(user);
		logger.info("User created");
	}
}
