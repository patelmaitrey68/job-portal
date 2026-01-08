package com.jobportal.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

import com.jobportal.dto.ApplicationDTO;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JobRepository jobRepository;
	
	@PostMapping("/job/{jobId}")
	public ResponseEntity<ApplicationDTO> applyToJob(
			@PathVariable String jobId,
			@Valid @RequestBody ApplicationDTO applicationDTO,
			Authentication authentication) {
		User applicant = getCurrentUser(authentication);
		ApplicationDTO created = applicationService.applyToJob(jobId, applicationDTO, applicant);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable String id) {
		ApplicationDTO application = applicationService.getApplicationById(id);
		return ResponseEntity.ok(application);
	}
	
	@GetMapping
	public ResponseEntity<Page<ApplicationDTO>> getMyApplications(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Authentication authentication) {
		User applicant = getCurrentUser(authentication);
		Pageable pageable = PageRequest.of(page, size);
		Page<ApplicationDTO> applications = applicationService.getApplicationsByApplicant(applicant, pageable);
		return ResponseEntity.ok(applications);
	}
	
	@GetMapping("/job/{jobId}")
	public ResponseEntity<Page<ApplicationDTO>> getJobApplications(
			@PathVariable String jobId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Authentication authentication) {
		User recruiter = getCurrentUser(authentication);
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new RuntimeException("Job not found"));
		
		// Verify recruiter owns the job
		if (!job.getPostedBy().getId().equals(recruiter.getId())) {
			throw new RuntimeException("You don't have permission to view these applications");
		}
		
		Pageable pageable = PageRequest.of(page, size);
		Page<ApplicationDTO> applications = applicationService.getApplicationsByJob(job, pageable);
		return ResponseEntity.ok(applications);
	}
	
	@PutMapping("/{id}/status")
	public ResponseEntity<ApplicationDTO> updateApplicationStatus(
			@PathVariable String id,
			@RequestParam String status,
			@RequestParam(required = false) String notes,
			@RequestParam(required = false) String rejectionReason,
			Authentication authentication) {
		User recruiter = getCurrentUser(authentication);
		ApplicationDTO updated = applicationService.updateApplicationStatus(id, status, recruiter, notes, rejectionReason);
		return ResponseEntity.ok(updated);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> withdrawApplication(
			@PathVariable String id,
			Authentication authentication) {
		User applicant = getCurrentUser(authentication);
		applicationService.withdrawApplication(id, applicant);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/status/{status}")
	public ResponseEntity<List<ApplicationDTO>> getApplicationsByStatus(
			@PathVariable String status,
			Authentication authentication) {
		User applicant = getCurrentUser(authentication);
		List<ApplicationDTO> applications = applicationService.getApplicationsByStatus(applicant, status);
		return ResponseEntity.ok(applications);
	}
	
	private User getCurrentUser(Authentication authentication) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}
}

