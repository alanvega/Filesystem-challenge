package com.alan.filesystemchallenge.repositories;

import com.alan.filesystemchallenge.models.entities.File;
import org.springframework.data.repository.CrudRepository;

public interface FilesRepository extends CrudRepository<File, Long> {
}
