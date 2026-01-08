package com.jobportal.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.entity.Notification;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public ResponseEntity<Page<Notification>> getNotifications(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		Pageable pageable = PageRequest.of(page, size);
		Page<Notification> notifications = notificationService.getNotificationsByUser(user, pageable);
		return ResponseEntity.ok(notifications);
	}
	
	@GetMapping("/unread")
	public ResponseEntity<List<Notification>> getUnreadNotifications(Authentication authentication) {
		User user = getCurrentUser(authentication);
		List<Notification> notifications = notificationService.getUnreadNotifications(user);
		return ResponseEntity.ok(notifications);
	}
	
	@GetMapping("/unread/count")
	public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
		User user = getCurrentUser(authentication);
		long count = notificationService.getUnreadCount(user);
		return ResponseEntity.ok(count);
	}
	
	@PutMapping("/{id}/read")
	public ResponseEntity<Notification> markAsRead(
			@PathVariable String id,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		Notification notification = notificationService.markAsRead(id, user);
		return ResponseEntity.ok(notification);
	}
	
	@PutMapping("/read-all")
	public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
		User user = getCurrentUser(authentication);
		notificationService.markAllAsRead(user);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteNotification(
			@PathVariable String id,
			Authentication authentication) {
		User user = getCurrentUser(authentication);
		notificationService.deleteNotification(id, user);
		return ResponseEntity.noContent().build();
	}
	
	private User getCurrentUser(Authentication authentication) {
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}
}

