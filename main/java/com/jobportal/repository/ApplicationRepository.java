package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;

public interface ApplicationRepository extends MongoRepository<Application, String> {
	
	// Find applications by job
	List<Application> findByJobId(Job jobId);
	
	Page<Application> findByJobId(Job jobId, Pageable pageable);
	
	// Find applications by applicant
	List<Application> findByApplicantId(User applicantId);
	
	Page<Application> findByApplicantId(User applicantId, Pageable pageable);
	
	// Find applications by status
	List<Application> findByStatus(String status);
	
	// Find applications by job and status
	List<Application> findByJobIdAndStatus(Job jobId, String status);
	
	// Find applications by applicant and status
	List<Application> findByApplicantIdAndStatus(User applicantId, String status);
	
	// Check if application exists
	Optional<Application> findByJobIdAndApplicantId(Job jobId, User applicantId);
	
	// Count applications by job
	long countByJobId(Job jobId);
	
	// Count applications by applicant
	long countByApplicantId(User applicantId);
	
	// Count applications by status
	long countByStatus(String status);
	
	// Find applications ordered by applied date
	List<Application> findByApplicantIdOrderByAppliedAtDesc(User applicantId);
	
	// Find applications ordered by match score
	@Query("{ jobId: ?0 }")
	List<Application> findByJobIdOrderByMatchScoreDesc(Job jobId);
}

