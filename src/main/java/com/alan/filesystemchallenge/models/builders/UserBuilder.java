package com.alan.filesystemchallenge.models.builders;

import com.alan.filesystemchallenge.models.entities.Users;

public class UserBuilder {
	private String username;
	private String password;

	public static UserBuilder builder() {
		return new UserBuilder();
	}

	public UserBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	public UserBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	public Users build() {
		var users = new Users();
		users.setUsername(this.username);
		users.setPassword(this.password);
		return users;
	}
}
