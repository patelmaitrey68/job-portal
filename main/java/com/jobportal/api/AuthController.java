package com.jobportal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.AuthResponse;
import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.RegisterRequest;
import com.jobportal.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}

	// Send verification email
	@PostMapping("/send-verification-email")
	public ResponseEntity<?> sendVerificationEmail(@RequestParam String email) {
		authService.sendVerificationEmail(email);
		return ResponseEntity.ok("Verification email sent");
	}

	// Verify email
	@GetMapping("/verify-email")
	public ResponseEntity<?> verifyEmail(@RequestParam String token) {
		boolean verified = authService.verifyEmail(token);
		if (verified) {
			return ResponseEntity.ok("Email verified successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
		}
	}

	// Request password reset
	@PostMapping("/request-password-reset")
	public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
		authService.sendPasswordResetEmail(email);
		return ResponseEntity.ok("Password reset email sent");
	}

	// Reset password
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
		boolean reset = authService.resetPassword(token, newPassword);
		if (reset) {
			return ResponseEntity.ok("Password reset successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
		}
	}
}

