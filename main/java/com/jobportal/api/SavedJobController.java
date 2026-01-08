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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.entity.SavedJob;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.SavedJobService;

@RestController
@RequestMapping("/api/saved-jobs")
@CrossOrigin(origins = "*")
public class SavedJobController {
	
	@Autowired
	private SavedJobService savedJobService;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/{jobId}")
	public ResponseEntity<SavedJob> saveJob(
			@PathVariable String jobId,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		SavedJob savedJob = savedJobService.saveJob(jobId, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedJob);
	}
	
	@DeleteMapping("/{jobId}")
	public ResponseEntity<Void> unsaveJob(
			@PathVariable String jobId,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		savedJobService.unsaveJob(jobId, user);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{jobId}/check")
	public ResponseEntity<Boolean> isJobSaved(
			@PathVariable String jobId,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		boolean isSaved = savedJobService.isJobSaved(jobId, user);
		return ResponseEntity.ok(isSaved);
	}
	
	@GetMapping
	public ResponseEntity<Page<SavedJob>> getSavedJobs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		Pageable pageable = PageRequest.of(page, size);
		Page<SavedJob> savedJobs = savedJobService.getSavedJobs(user, pageable);
		return ResponseEntity.ok(savedJobs);
	}
	
	private User getCurrentUser(Authentication authentication) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}
}

