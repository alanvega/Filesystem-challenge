package com.alan.filesystemchallenge.models.builders;

import com.alan.filesystemchallenge.models.entities.Users;

public class UserBuilder {
	private String username;
	private String password;
	private Long userId;

	public static UserBuilder builder() {
		return new UserBuilder();
	}

	public UserBuilder withUsername(String username) {
		this.username = username;
		return this;
	}

	public UserBuilder withPassword(String password) {
		this.password = password;
		return this;
	}

	public UserBuilder withUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public Users build() {
		var users = new Users();
		users.setUsername(this.username);
		users.setPassword(this.password);
		users.setId(this.userId);
		return users;
	}
}
