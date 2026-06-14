package com.jobportal.entity;

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
@Table(name = "companies")
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@OneToOne
	@JoinColumn(unique = true)
	private User recruiterId; // Recruiter reference
	
	
	private String companyName; // Required
	
	private String companyLogo;
	
	
	private String description;
	
	private String website; // URL
	
	private String industry;
	
	private String companySize; // 1-10, 11-50, 51-200, 201-500, 500+
	
	private String location;
	
	private String address;
	
	private String phone;
	
	private String email;
	
	@Embedded
	private SocialMedia socialMedia;
	
	private Integer foundedYear;
	
	@CreationTimestamp
	private java.time.LocalDateTime createdAt;
	
	@UpdateTimestamp
	private java.time.LocalDateTime updatedAt;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Embeddable
	public static class SocialMedia {
		private String linkedin;
		private String twitter;
		private String facebook;
	}
}

