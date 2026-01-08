package com.jobportal.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.JobDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/stats")
	public ResponseEntity<Map<String, Object>> getPlatformStatistics() {
		Map<String, Object> stats = adminService.getPlatformStatistics();
		return ResponseEntity.ok(stats);
	}
	
	@GetMapping("/users")
	public ResponseEntity<Page<UserDTO>> getAllUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<UserDTO> users = adminService.getAllUsers(pageable);
		return ResponseEntity.ok(users);
	}
	
	@PutMapping("/users/{id}/status")
	public ResponseEntity<UserDTO> updateUserStatus(
			@PathVariable String id,
			@RequestParam Boolean isActive) {
		UserDTO user = adminService.updateUserStatus(id, isActive);
		return ResponseEntity.ok(user);
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable String id) {
		adminService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/jobs")
	public ResponseEntity<Page<JobDTO>> getAllJobs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<JobDTO> jobs = adminService.getAllJobs(pageable);
		return ResponseEntity.ok(jobs);
	}
	
	@DeleteMapping("/jobs/{id}")
	public ResponseEntity<Void> deleteJob(@PathVariable String id) {
		adminService.deleteJob(id);
		return ResponseEntity.noContent().build();
	}
}

