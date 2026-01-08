package com.jobportal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.jobportal.entity.Notification;
import com.jobportal.entity.User;

public interface NotificationRepository extends MongoRepository<Notification, String> {
	
	// Find notifications by user
	List<Notification> findByUserIdOrderByCreatedAtDesc(User userId);
	
	Page<Notification> findByUserIdOrderByCreatedAtDesc(User userId, Pageable pageable);
	
	// Find unread notifications
	List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(User userId);
	
	// Count unread notifications
	long countByUserIdAndReadFalse(User userId);
	
	// Find notifications by type
	List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(User userId, String type);
}

