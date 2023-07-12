package com.alan.filesystemchallenge.repositories;

import com.alan.filesystemchallenge.models.entities.FileShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileShareRepository extends JpaRepository<FileShare, Long> {
	List<FileShare> findByUserId(Long userId);
	List<FileShare> findByFileId(Long fileId);
	void deleteByFileId(Long fileId);
}
