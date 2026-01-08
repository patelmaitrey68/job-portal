package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.jobportal.entity.Job;
import com.jobportal.entity.SavedJob;
import com.jobportal.entity.User;

public interface SavedJobRepository extends MongoRepository<SavedJob, String> {
	
	// Find saved jobs by user
	List<SavedJob> findByUserIdOrderBySavedAtDesc(User userId);
	
	Page<SavedJob> findByUserIdOrderBySavedAtDesc(User userId, Pageable pageable);
	
	// Check if job is saved
	Optional<SavedJob> findByUserIdAndJobId(User userId, Job jobId);
	
	// Count saved jobs by user
	long countByUserId(User userId);
	
	// Delete saved job
	void deleteByUserIdAndJobId(User userId, Job jobId);
}

