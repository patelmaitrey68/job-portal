package com.jobportal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import com.jobportal.entity.Job;
import com.jobportal.entity.User;

public interface JobRepository extends JpaRepository<Job, String> {
	
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
	@org.springframework.data.jpa.repository.Query("SELECT j FROM Job j WHERE (LOWER(j.title) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(j.company) LIKE LOWER(CONCAT('%', ?1, '%'))) AND j.status = 'active'")
	List<Job> searchJobs(String searchText);
	
	// Find jobs by multiple filters
	@org.springframework.data.jpa.repository.Query("SELECT j FROM Job j WHERE j.status = ?1 AND j.location = ?2 AND j.jobType = ?3")
	List<Job> findByFilters(String status, String location, String jobType);
	
	// Find featured jobs
	List<Job> findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(String status);
	
	// Count jobs by recruiter
	long countByPostedBy(User postedBy);
	
	// Count active jobs
	long countByStatus(String status);
}

