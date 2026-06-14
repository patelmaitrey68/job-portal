package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;





import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications")
// @CompoundIndex(name = "job_applicant_idx", def = "{'jobId': 1, 'applicantId': 1}", unique = true)
public class Application {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@ManyToOne
	private Job jobId; // Job reference
	
	@ManyToOne
	private User applicantId; // Applicant reference
	
	private String resumeUrl; // Required
	
	private String resumeFileName;
	
	private String coverLetter; // Max 2000 characters
	
	
	private String status = "pending"; // pending, shortlisted, rejected, hired, withdrawn
	
	@CreationTimestamp
	private LocalDateTime appliedAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	private LocalDateTime reviewedAt;
	
	@ManyToOne
	private User reviewedBy; // Recruiter who reviewed
	
	private String notes; // Internal notes by recruiter
	
	private String rejectionReason; // If rejected
	
	private LocalDateTime interviewDate; // If shortlisted
	
	// AI-based matching features
	private Double matchScore; // 0-100
	
	@ElementCollection
	private List<String> skillsMatch; // Matched skills from resume
}

