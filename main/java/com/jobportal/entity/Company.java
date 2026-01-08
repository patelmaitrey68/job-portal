package com.jobportal.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "companies")
public class Company {
	@Id
	private String id;
	
	@Indexed(unique = true)
	@DBRef
	private User recruiterId; // Recruiter reference
	
	@TextIndexed
	private String companyName; // Required
	
	private String companyLogo;
	
	@TextIndexed
	private String description;
	
	private String website; // URL
	
	private String industry;
	
	private String companySize; // 1-10, 11-50, 51-200, 201-500, 500+
	
	private String location;
	
	private String address;
	
	private String phone;
	
	private String email;
	
	private SocialMedia socialMedia;
	
	private Integer foundedYear;
	
	@CreatedDate
	private java.time.LocalDateTime createdAt;
	
	@LastModifiedDate
	private java.time.LocalDateTime updatedAt;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SocialMedia {
		private String linkedin;
		private String twitter;
		private String facebook;
	}
}

