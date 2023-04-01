package com.alan.filesystemchallenge.services;

import com.alan.filesystemchallenge.exceptions.UserAlreadyExistsException;
import com.alan.filesystemchallenge.exceptions.UserEmptyException;
import com.alan.filesystemchallenge.models.builders.UserBuilder;
import com.alan.filesystemchallenge.models.entities.Users;
import com.alan.filesystemchallenge.models.requests.UserRequest;
import com.alan.filesystemchallenge.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertThrows;

class UserServiceTest {

	private UserService userService;

	private UsersRepository usersRepository;
	private PasswordEncoder passwordEncoder;


	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ENCODED_PASSWORD = "{bcrypt}p#s$w0rdEnc0ded";

	@BeforeEach
	void setUp() {
		this.usersRepository = mock(UsersRepository.class);
		this.passwordEncoder = mock(PasswordEncoder.class);
		userService = new UserService(usersRepository, passwordEncoder);
	}

	@Test
	void create() {
		var userRequest = new UserRequest();
		userRequest.setUsername(USERNAME);
		userRequest.setPassword(PASSWORD);

		when(this.usersRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.empty());

		when(this.passwordEncoder.encode(userRequest.getPassword())).thenReturn(ENCODED_PASSWORD);

		this.userService.create(userRequest);

		verify(this.usersRepository, times(1)).findByUsername(USERNAME);
		verify(this.passwordEncoder, times(1)).encode(PASSWORD);
		verify(this.usersRepository, times(1)).save(any(Users.class));

	}

	@Test
	void createEmptyPasswordThrowsUserEmptyException() {
		var userRequest = new UserRequest();
		userRequest.setUsername(USERNAME);

		assertThrows(UserEmptyException.class, () -> this.userService.create(userRequest));

		verify(this.usersRepository, times(0)).findByUsername(USERNAME);
		verify(this.passwordEncoder, times(0)).encode(PASSWORD);
		verify(this.usersRepository, times(0)).save(any(Users.class));

	}

	@Test
	void createThrowsUserAlreadyExistsException() {
		var userRequest = new UserRequest();
		userRequest.setUsername(USERNAME);
		userRequest.setPassword(PASSWORD);

		var user = UserBuilder.builder()
				.withUserId(1L)
				.withUsername(USERNAME)
				.withPassword(ENCODED_PASSWORD)
				.build();

		when(this.usersRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.of(user));

		assertThrows(UserAlreadyExistsException.class, () -> this.userService.create(userRequest));

		verify(this.usersRepository, times(1)).findByUsername(USERNAME);
		verify(this.passwordEncoder, times(0)).encode(PASSWORD);
		verify(this.usersRepository, times(0)).save(any(Users.class));

	}
}
