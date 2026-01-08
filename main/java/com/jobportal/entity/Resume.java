package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "resumes")
public class Resume {
	@Id
	private String id;
	
	@Indexed
	@DBRef
	private User userId; // User reference
	
	private String fileName; // Required
	
	private String fileUrl; // Required - Cloud storage URL
	
	private Long fileSize; // Size in bytes
	
	private String fileType; // pdf, doc, docx
	
	@Indexed
	private Boolean isDefault = true;
	
	@CreatedDate
	private LocalDateTime uploadedAt;
	
	// AI-extracted data (optional)
	private ParsedData parsedData;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ParsedData {
		private List<String> skills;
		private Integer experience; // Years of experience
		private List<String> education;
		private List<String> certifications;
	}
}

