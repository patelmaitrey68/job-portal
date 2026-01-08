package com.jobportal.dto;

import com.jobportal.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private String id;
	
	@NotBlank(message = "{user.name.absent}")
	private String name;
	
	@NotBlank(message = "{user.email.absent}")
	@Email(message = "{user.email.invalid}")
	private String email;
	
	private String password; // Not included in responses
	
	private AccountType accountType;
}

