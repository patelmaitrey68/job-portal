package com.jobportal.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
	private String id;
	private String title;
	private String company;
	private String companyLogo;
	private String description;
	private List<String> requirements;
	private List<String> responsibilities;
	private String location;
	private String jobType;
	private String experienceLevel;
	private Double salaryMin;
	private Double salaryMax;
	private String salaryCurrency;
	private String category;
	private String industry;
	private List<String> skills;
	private String postedBy; // User ID
	private String postedByName; // User name
	private String status;
	private LocalDateTime applicationDeadline;
	private Integer applicationCount;
	private Integer views;
	private Boolean isFeatured;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

