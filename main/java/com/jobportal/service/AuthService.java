package com.jobportal.service;

import com.jobportal.dto.AuthResponse;
import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.RegisterRequest;

public interface AuthService {
	AuthResponse register(RegisterRequest request);
	AuthResponse login(LoginRequest request);

	// Email verification
	void sendVerificationEmail(String email);
	boolean verifyEmail(String token);

	// Password reset
	void sendPasswordResetEmail(String email);
	boolean resetPassword(String token, String newPassword);
}

