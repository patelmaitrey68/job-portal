package com.jobportal.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
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
@Document(collection = "saved_jobs")
@CompoundIndex(name = "user_job_idx", def = "{'userId': 1, 'jobId': 1}", unique = true)
public class SavedJob {
	@Id
	private String id;
	
	@Indexed
	@DBRef
	private User userId; // User reference
	
	@Indexed
	@DBRef
	private Job jobId; // Job reference
	
	@CreatedDate
	private LocalDateTime savedAt;
}

