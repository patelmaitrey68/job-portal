package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jobportal.entity.Resume;
import com.jobportal.entity.User;

public interface ResumeRepository extends MongoRepository<Resume, String> {
	
	// Find resumes by user
	List<Resume> findByUserId(User userId);
	
	// Find default resume for user
	Optional<Resume> findByUserIdAndIsDefaultTrue(User userId);
	
	// Find resume by user and ID
	Optional<Resume> findByIdAndUserId(String id, User userId);
	
	// Count resumes by user
	long countByUserId(User userId);
}

