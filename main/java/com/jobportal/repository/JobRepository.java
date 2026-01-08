package com.jobportal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;

public interface JobRepository extends MongoRepository<Job, String> {
	
	// Find jobs by recruiter
	List<Job> findByPostedBy(User postedBy);
	
	Page<Job> findByPostedBy(User postedBy, Pageable pageable);
	
	// Find jobs by status
	List<Job> findByStatus(String status);
	
	Page<Job> findByStatus(String status, Pageable pageable);
	
	// Find jobs by location
	List<Job> findByLocation(String location);
	
	// Find jobs by job type
	List<Job> findByJobType(String jobType);
	
	// Find jobs by category
	List<Job> findByCategory(String category);
	
	// Find active jobs
	List<Job> findByStatusOrderByCreatedAtDesc(String status);
	
	Page<Job> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
	
	// Search jobs by text
	@Query("{ $text: { $search: ?0 }, status: 'active' }")
	List<Job> searchJobs(String searchText);
	
	// Find jobs by multiple filters
	@Query("{ status: ?0, location: ?1, jobType: ?2 }")
	List<Job> findByFilters(String status, String location, String jobType);
	
	// Find featured jobs
	List<Job> findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(String status);
	
	// Count jobs by recruiter
	long countByPostedBy(User postedBy);
	
	// Count active jobs
	long countByStatus(String status);
}

