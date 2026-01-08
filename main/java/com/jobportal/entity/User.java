package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jobportal.dto.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
	@Id
	private String id;
	
	private String name;
	
	@Indexed(unique = true)
	private String email;
	
	private String password;
	
	private AccountType accountType; // APPLICANT, EMPLOYER, ADMIN
	
	private String profilePicture;
	
	private String phone;
	
	private String location;
	
	private String bio; // Max 500 characters
	
	// For Job Seekers
	private List<String> skills;
	
	private List<Experience> experience;
	
	private List<Education> education;
	
	private Boolean isActive = true;
	
	private Boolean isEmailVerified = false;
	
	private String emailVerificationToken;
	
	private String resetPasswordToken;
	
	private LocalDateTime resetPasswordExpires;
	
	@CreatedDate
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	public com.jobportal.dto.UserDTO toDTO() {
		com.jobportal.dto.UserDTO dto = new com.jobportal.dto.UserDTO();
		dto.setId(this.id);
		dto.setName(this.name);
		dto.setEmail(this.email);
		dto.setAccountType(this.accountType);
		return dto;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Experience {
		private String company;
		private String position;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private Boolean current = false;
		private String description;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Education {
		private String institution;
		private String degree;
		private String field;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private Boolean current = false;
	}
}
