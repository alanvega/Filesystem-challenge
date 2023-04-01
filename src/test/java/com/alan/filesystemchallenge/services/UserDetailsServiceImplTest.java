package com.alan.filesystemchallenge.services;

import com.alan.filesystemchallenge.exceptions.UserNotFoundException;
import com.alan.filesystemchallenge.models.builders.UserBuilder;
import com.alan.filesystemchallenge.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertThrows;

class UserDetailsServiceImplTest {

	private UserDetailsServiceImpl userDetailsService;

	private UsersRepository usersRepository;

	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";

	@BeforeEach
	void setUp() {
		usersRepository = mock(UsersRepository.class);
		userDetailsService = new UserDetailsServiceImpl(usersRepository);
	}

	@Test
	void loadUserByUsername() {
		var user = UserBuilder.builder()
				.withUserId(1L)
				.withUsername(USERNAME)
				.withPassword(PASSWORD)
				.build();

		when(this.usersRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

		var userDetails = this.userDetailsService.loadUserByUsername(USERNAME);

		assertEquals(USERNAME, userDetails.getUsername());
		assertEquals(PASSWORD, userDetails.getPassword());

		verify(this.usersRepository, times(1)).findByUsername(USERNAME);
	}

	@Test
	void loadUserByUsernameThrowsUserNotFoundException() {
		when(this.usersRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> this.userDetailsService.loadUserByUsername(USERNAME));

		verify(this.usersRepository, times(1)).findByUsername(USERNAME);
	}
}
