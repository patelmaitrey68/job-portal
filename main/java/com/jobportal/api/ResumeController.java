package com.jobportal.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ResumeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "*")
public class ResumeController {
	
	@Autowired
	private ResumeService resumeService;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping
	public ResponseEntity<Resume> uploadResume(
			@Valid @RequestBody Resume resume,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		Resume uploaded = resumeService.uploadResume(resume, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(uploaded);
	}
	
	@GetMapping
	public ResponseEntity<List<Resume>> getMyResumes(Authentication authentication) {
		User user = getCurrentUser(authentication);
		List<Resume> resumes = resumeService.getResumesByUser(user);
		return ResponseEntity.ok(resumes);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Resume> getResumeById(
			@PathVariable String id,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		Resume resume = resumeService.getResumeById(id, user);
		return ResponseEntity.ok(resume);
	}
	
	@GetMapping("/default")
	public ResponseEntity<Resume> getDefaultResume(Authentication authentication) {
		User user = getCurrentUser(authentication);
		Resume resume = resumeService.getDefaultResume(user);
		return ResponseEntity.ok(resume);
	}
	
	@PutMapping("/{id}/default")
	public ResponseEntity<Resume> setAsDefault(
			@PathVariable String id,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		Resume resume = resumeService.setAsDefault(id, user);
		return ResponseEntity.ok(resume);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteResume(
			@PathVariable String id,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		resumeService.deleteResume(id, user);
		return ResponseEntity.noContent().build();
	}
	
	private User getCurrentUser(Authentication authentication) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}
}

