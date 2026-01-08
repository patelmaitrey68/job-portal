package com.jobportal.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.JobDTO;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {
	
	@Autowired
	private JobService jobService;
	
	@Autowired
	private UserRepository userRepository;
	
	@PreAuthorize("hasRole('EMPLOYER')")
	@PostMapping
	public ResponseEntity<JobDTO> createJob(@Valid @RequestBody JobDTO jobDTO, Authentication authentication) {
		User recruiter = getCurrentUser(authentication);
		JobDTO createdJob = jobService.createJob(jobDTO, recruiter);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
	}

	@PreAuthorize("hasRole('EMPLOYER')")
	@PutMapping("/{id}")
	public ResponseEntity<JobDTO> updateJob(@PathVariable String id, @Valid @RequestBody JobDTO jobDTO, Authentication authentication) {
		User recruiter = getCurrentUser(authentication);
		JobDTO updatedJob = jobService.updateJob(id, jobDTO, recruiter);
		return ResponseEntity.ok(updatedJob);
	}

	@PreAuthorize("hasRole('EMPLOYER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteJob(@PathVariable String id, Authentication authentication) {
		User recruiter = getCurrentUser(authentication);
		jobService.deleteJob(id, recruiter);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<JobDTO> getJobById(@PathVariable String id) {
		JobDTO job = jobService.getJobById(id);
		return ResponseEntity.ok(job);
	}
	
	@GetMapping
	public ResponseEntity<Page<JobDTO>> getAllJobs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) String jobType,
			@RequestParam(required = false) String category) {
		Pageable pageable = PageRequest.of(page, size);
		Page<JobDTO> jobs;
		
		// Use filterJobs if any filter is provided
		if ((location != null && !location.isEmpty()) || 
		    (jobType != null && !jobType.isEmpty()) || 
		    (category != null && !category.isEmpty())) {
			jobs = jobService.filterJobs(location, jobType, category, pageable);
		} else {
			jobs = jobService.getAllJobs(pageable);
		}
		
		return ResponseEntity.ok(jobs);
	}
	
	@GetMapping("/my-jobs")
	public ResponseEntity<Page<JobDTO>> getMyJobs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Authentication authentication) {
		User recruiter = getCurrentUser(authentication);
		Pageable pageable = PageRequest.of(page, size);
		Page<JobDTO> jobs = jobService.getJobsByRecruiter(recruiter, pageable);
		return ResponseEntity.ok(jobs);
	}
	
	@GetMapping("/featured")
	public ResponseEntity<List<JobDTO>> getFeaturedJobs() {
		List<JobDTO> jobs = jobService.getFeaturedJobs();
		return ResponseEntity.ok(jobs);
	}
	
	@GetMapping("/search")
	public ResponseEntity<Page<JobDTO>> searchJobs(
			@RequestParam String q,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<JobDTO> jobs = jobService.searchJobs(q, pageable);
		return ResponseEntity.ok(jobs);
	}
	
	private User getCurrentUser(Authentication authentication) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}
}

