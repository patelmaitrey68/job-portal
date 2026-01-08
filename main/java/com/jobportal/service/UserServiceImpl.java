package com.jobportal.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.dto.UserDTO;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.exception.JobPortalException;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDTO getUserProfile(User user) {
		return user.toDTO();
	}

	@Override
	public UserDTO updateUserProfile(User user, UserDTO userDTO) {
		user.setName(userDTO.getName());
		if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
			// Check if new email is already taken
			if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
				   throw new JobPortalException("Email already exists");
			}
			user.setEmail(userDTO.getEmail().toLowerCase());
		}
		user.setUpdatedAt(LocalDateTime.now());
		
		user = userRepository.save(user);
		return user.toDTO();
	}

	@Override
	public void changePassword(User user, String oldPassword, String newPassword) {
		// Verify old password
		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			   throw new JobPortalException("Current password is incorrect");
		}
		
		// Set new password
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setUpdatedAt(LocalDateTime.now());
		userRepository.save(user);
	}

	@Override
	public UserDTO getUserById(String userId) {
		User user = userRepository.findById(userId)
				   .orElseThrow(() -> new JobPortalException("User not found"));
		return user.toDTO();
	}
}

