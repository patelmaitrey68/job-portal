package com.jobportal.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
	private String id;
	private String jobId;
	private String jobTitle;
	private String companyName;
	private String applicantId;
	private String applicantName;
	private String resumeUrl;
	private String resumeFileName;
	private String coverLetter;
	private String status;
	private LocalDateTime appliedAt;
	private LocalDateTime updatedAt;
	private LocalDateTime reviewedAt;
	private String reviewedBy;
	private String notes;
	private String rejectionReason;
	private LocalDateTime interviewDate;
	private Double matchScore;
	private List<String> skillsMatch;
}

