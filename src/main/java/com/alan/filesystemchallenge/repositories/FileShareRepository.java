package com.alan.filesystemchallenge.repositories;

import com.alan.filesystemchallenge.models.entities.FileShare;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileShareRepository extends JpaRepository<FileShare, Long> {}
