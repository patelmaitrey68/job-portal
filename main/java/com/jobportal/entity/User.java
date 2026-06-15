package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;



import com.jobportal.dto.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	private String name;
	
	@Column(unique = true)
	private String email;
	
	private String password;
	
	private AccountType accountType; // APPLICANT, EMPLOYER, ADMIN
	
	private String profilePicture;
	
	private String phone;
	
	private String location;
	
	private String bio; // Max 500 characters
	
	private String companyName; // For Employers
	
	// For Job Seekers
	@ElementCollection
	@CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "skill")
	private List<String> skills;
	
	@ElementCollection
	@CollectionTable(name = "user_experience", joinColumns = @JoinColumn(name = "user_id"))
	private List<Experience> experience;
	
	@ElementCollection
	@CollectionTable(name = "user_education", joinColumns = @JoinColumn(name = "user_id"))
	private List<Education> education;
	
	private Boolean isActive = true;
	
	private Boolean isEmailVerified = false;
	
	private String emailVerificationToken;
	
	private String resetPasswordToken;
	
	private LocalDateTime resetPasswordExpires;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	public com.jobportal.dto.UserDTO toDTO() {
		com.jobportal.dto.UserDTO dto = new com.jobportal.dto.UserDTO();
		dto.setId(this.id);
		dto.setName(this.name);
		dto.setEmail(this.email);
		dto.setAccountType(this.accountType);
		dto.setCompanyName(this.companyName);
		return dto;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
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
	@Embeddable
	public static class Education {
		private String institution;
		private String degree;
		private String field;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private Boolean current = false;
	}
}
