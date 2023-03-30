package com.alan.filesystemchallenge.repositories;

import com.alan.filesystemchallenge.models.entities.File;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FilesRepository extends CrudRepository<File, Long> {
	@Transactional
	@Modifying
	@Query("update files f set f.name = ?1 where f.id = ?2")
	void updateName(String name, Long id);

}
