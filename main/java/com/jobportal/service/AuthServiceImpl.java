
package com.jobportal.service;
import com.jobportal.exception.JobPortalException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.dto.AuthResponse;
import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.RegisterRequest;
import com.jobportal.dto.UserDTO;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;

import com.jobportal.utility.JwtUtil;
import com.jobportal.utility.EmailUtil;
import java.util.UUID;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private EmailUtil emailUtil;


	@Override
	public AuthResponse register(RegisterRequest request) {
		// Check if user already exists
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new JobPortalException("User already exists with email: " + request.getEmail());
		}

		// Create new user
		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail().toLowerCase());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setAccountType(request.getAccountType());
		user.setIsActive(true);
		user.setIsEmailVerified(false);
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());

		// Generate email verification token
		String verificationToken = UUID.randomUUID().toString();
		user.setEmailVerificationToken(verificationToken);

		user = userRepository.save(user);

		// Send verification email
		String verifyLink = "http://localhost:3000/verify-email?token=" + verificationToken;
		String subject = "Verify your email address";
		String text = "Welcome to Job Portal! Please verify your email by clicking the link: " + verifyLink;
		emailUtil.sendEmail(user.getEmail(), subject, text);

		// Generate JWT token
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		String token = jwtUtil.generateToken(userDetails, user.getId(), user.getAccountType());

		// Create UserDTO
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setName(user.getName());
		userDTO.setEmail(user.getEmail());
		userDTO.setAccountType(user.getAccountType());

		return new AuthResponse(token, "Bearer", userDTO);
	}
	@Override
	public void sendVerificationEmail(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new JobPortalException("User not found"));
		if (user.getIsEmailVerified() != null && user.getIsEmailVerified()) {
			throw new JobPortalException("Email already verified");
		}
		String verificationToken = UUID.randomUUID().toString();
		user.setEmailVerificationToken(verificationToken);
		userRepository.save(user);
		String verifyLink = "http://localhost:3000/verify-email?token=" + verificationToken;
		String subject = "Verify your email address";
		String text = "Please verify your email by clicking the link: " + verifyLink;
		emailUtil.sendEmail(user.getEmail(), subject, text);
	}

	@Override
	public boolean verifyEmail(String token) {
		User user = userRepository.findAll().stream()
			.filter(u -> token.equals(u.getEmailVerificationToken()))
			.findFirst()
			.orElse(null);
		if (user == null) return false;
		user.setIsEmailVerified(true);
		user.setEmailVerificationToken(null);
		userRepository.save(user);
		return true;
	}

	@Override
	public void sendPasswordResetEmail(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new JobPortalException("User not found"));
		String resetToken = UUID.randomUUID().toString();
		user.setResetPasswordToken(resetToken);
		user.setResetPasswordExpires(LocalDateTime.now().plusHours(1));
		userRepository.save(user);
		String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
		String subject = "Reset your password";
		String text = "To reset your password, click the link: " + resetLink;
		emailUtil.sendEmail(user.getEmail(), subject, text);
	}

	@Override
	public boolean resetPassword(String token, String newPassword) {
		User user = userRepository.findAll().stream()
			.filter(u -> token.equals(u.getResetPasswordToken()) && u.getResetPasswordExpires() != null && u.getResetPasswordExpires().isAfter(LocalDateTime.now()))
			.findFirst()
			.orElse(null);
		if (user == null) return false;
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setResetPasswordToken(null);
		user.setResetPasswordExpires(null);
		userRepository.save(user);
		return true;
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		// Authenticate user
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()
				)
		);
		
		// Load user details
		UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
		User user = userRepository.findByEmail(request.getEmail())
				   .orElseThrow(() -> new JobPortalException("User not found"));
		
		// Generate JWT token
		String token = jwtUtil.generateToken(userDetails, user.getId(), user.getAccountType());
		
		// Create UserDTO
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setName(user.getName());
		userDTO.setEmail(user.getEmail());
		userDTO.setAccountType(user.getAccountType());
		
		return new AuthResponse(token, "Bearer", userDTO);
	}
}

