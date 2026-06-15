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
@Table(name = "jobs")
public class Job {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	
	private String title; // Required, min 5, max 100
	
	
	private String company; // Required, min 2, max 100
	
	private String companyLogo;
	
	
	private String description; // Required, min 50
	
	@ElementCollection
	@CollectionTable(name = "job_requirements", joinColumns = @JoinColumn(name = "job_id"))
	@Column(name = "requirement")
	private List<String> requirements;
	
	@ElementCollection
	@CollectionTable(name = "job_responsibilities", joinColumns = @JoinColumn(name = "job_id"))
	@Column(name = "responsibility")
	private List<String> responsibilities;
	
	
	private String location; // Required
	
	
	private String jobType; // full-time, part-time, contract, internship, remote
	
	private String experienceLevel; // entry, mid, senior, executive
	
	private Double salaryMin;
	
	private Double salaryMax;
	
	private String salaryCurrency = "USD";
	
	private String category; // IT, Marketing, Finance, etc.
	
	private String industry;
	
	@ElementCollection
	@CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
	@Column(name = "skill")
	private List<String> skills; // Required skills
	
	@ManyToOne
	private User postedBy; // Recruiter reference
	
	
	private String status = "active"; // active, draft, closed, expired
	
	private LocalDateTime applicationDeadline;
	
	private Integer applicationCount = 0;
	
	private Integer views = 0;
	
	private Boolean isFeatured = false;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}

