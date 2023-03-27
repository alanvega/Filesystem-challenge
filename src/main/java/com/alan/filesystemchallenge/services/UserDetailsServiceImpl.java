package com.alan.filesystemchallenge.services;

import com.alan.filesystemchallenge.exceptions.UserNotFoundException;
import com.alan.filesystemchallenge.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

	private final UsersRepository usersRepository;

	@Autowired
	public UserDetailsServiceImpl(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Finding user with username {}...", username);
		var user = this.usersRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

		logger.info("User found");
		return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
	}

}
