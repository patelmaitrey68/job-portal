package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resumes")
public class Resume {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@ManyToOne
	private User userId; // User reference
	
	private String fileName; // Required
	
	private String fileUrl; // Required - Cloud storage URL
	
	private Long fileSize; // Size in bytes
	
	private String fileType; // pdf, doc, docx
	
	
	private Boolean isDefault = true;
	
	@CreationTimestamp
	private LocalDateTime uploadedAt;
	
	// AI-extracted data (optional)
	@Embedded
	private ParsedData parsedData;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	public static class ParsedData {
		@ElementCollection
	private List<String> skills;
		private Integer experience; // Years of experience
		private List<String> education;
		@ElementCollection
		private List<String> certifications;
	}
}

