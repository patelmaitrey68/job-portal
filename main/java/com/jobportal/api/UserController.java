package com.jobportal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.UserDTO;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/profile")
	public ResponseEntity<UserDTO> getProfile(Authentication authentication) {
		User user = getCurrentUser(authentication);
		UserDTO profile = userService.getUserProfile(user);
		return ResponseEntity.ok(profile);
	}
	
	@PutMapping("/profile")
	public ResponseEntity<UserDTO> updateProfile(
			@Valid @RequestBody UserDTO userDTO,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		UserDTO updated = userService.updateUserProfile(user, userDTO);
		return ResponseEntity.ok(updated);
	}
	
	@PutMapping("/password")
	public ResponseEntity<Void> changePassword(
			@RequestParam String oldPassword,
			@RequestParam String newPassword,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		userService.changePassword(user, oldPassword, newPassword);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
		UserDTO user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}
	
	private User getCurrentUser(Authentication authentication) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}
}

