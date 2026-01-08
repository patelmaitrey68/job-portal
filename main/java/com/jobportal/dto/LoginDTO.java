package com.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
	@NotBlank(message = "{user.email.absent}")
	@Email(message = "{user.email.invalid}")
	private String email;

	@NotBlank(message = "{user.password.absent}")
	private String password;
}

