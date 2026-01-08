package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "applications")
@CompoundIndex(name = "job_applicant_idx", def = "{'jobId': 1, 'applicantId': 1}", unique = true)
public class Application {
	@Id
	private String id;
	
	@Indexed
	@DBRef
	private Job jobId; // Job reference
	
	@Indexed
	@DBRef
	private User applicantId; // Applicant reference
	
	private String resumeUrl; // Required
	
	private String resumeFileName;
	
	private String coverLetter; // Max 2000 characters
	
	@Indexed
	private String status = "pending"; // pending, shortlisted, rejected, hired, withdrawn
	
	@CreatedDate
	private LocalDateTime appliedAt;
	
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	private LocalDateTime reviewedAt;
	
	@DBRef
	private User reviewedBy; // Recruiter who reviewed
	
	private String notes; // Internal notes by recruiter
	
	private String rejectionReason; // If rejected
	
	private LocalDateTime interviewDate; // If shortlisted
	
	// AI-based matching features
	private Double matchScore; // 0-100
	
	private List<String> skillsMatch; // Matched skills from resume
}

