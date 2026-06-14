package com.jobportal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;





import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
// @CompoundIndex(name = "user_read_created_idx", def = "{'userId': 1, 'read': 1, 'createdAt': -1}")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@ManyToOne
	private User userId; // User reference
	
	private String type; // application, status_update, new_job, message, system
	
	private String title; // Required, max 100
	
	private String message; // Required, max 500
	
	private String link; // URL to related page
	
	private String relatedId; // ID of related entity (Job, Application, etc.)
	
	
	private Boolean read = false;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
}

