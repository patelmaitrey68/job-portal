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
@Document(collection = "notifications")
@CompoundIndex(name = "user_read_created_idx", def = "{'userId': 1, 'read': 1, 'createdAt': -1}")
public class Notification {
	@Id
	private String id;
	
	@Indexed
	@DBRef
	private User userId; // User reference
	
	private String type; // application, status_update, new_job, message, system
	
	private String title; // Required, max 100
	
	private String message; // Required, max 500
	
	private String link; // URL to related page
	
	private String relatedId; // ID of related entity (Job, Application, etc.)
	
	@Indexed
	private Boolean read = false;
	
	@CreatedDate
	private LocalDateTime createdAt;
}

