package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "jobs")
public class Job {
	@Id
	private String id;
	
	@TextIndexed
	private String title; // Required, min 5, max 100
	
	@TextIndexed
	private String company; // Required, min 2, max 100
	
	private String companyLogo;
	
	@TextIndexed
	private String description; // Required, min 50
	
	private List<String> requirements;
	
	private List<String> responsibilities;
	
	@Indexed
	private String location; // Required
	
	@Indexed
	private String jobType; // full-time, part-time, contract, internship, remote
	
	private String experienceLevel; // entry, mid, senior, executive
	
	private Double salaryMin;
	
	private Double salaryMax;
	
	private String salaryCurrency = "USD";
	
	private String category; // IT, Marketing, Finance, etc.
	
	private String industry;
	
	private List<String> skills; // Required skills
	
	@Indexed
	@DBRef
	private User postedBy; // Recruiter reference
	
	@Indexed
	private String status = "active"; // active, draft, closed, expired
	
	private LocalDateTime applicationDeadline;
	
	private Integer applicationCount = 0;
	
	private Integer views = 0;
	
	private Boolean isFeatured = false;
	
	@CreatedDate
	private LocalDateTime createdAt;
	
	@LastModifiedDate
	private LocalDateTime updatedAt;
}

