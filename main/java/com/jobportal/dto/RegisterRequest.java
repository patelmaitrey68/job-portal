package com.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	private String name;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;
	
	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	private String password;
	
	@NotNull(message = "Account type is required")
	private AccountType accountType;
	
	private String companyName;
}

