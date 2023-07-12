package com.alan.filesystemchallenge.repositories;

import com.alan.filesystemchallenge.models.entities.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<Users, Long> {
	Optional<Users> findByUsername(String username);
}
