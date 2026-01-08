
package com.jobportal.service;
import com.jobportal.exception.JobPortalException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jobportal.entity.Notification;
import com.jobportal.entity.User;
import com.jobportal.repository.NotificationRepository;

@Service
public class NotificationServiceImpl implements NotificationService {
	
	@Autowired
	private NotificationRepository notificationRepository;

	@Override
	public Notification createNotification(Notification notification) {
		notification.setCreatedAt(LocalDateTime.now());
		notification.setRead(false);
		return notificationRepository.save(notification);
	}

	@Override
	public Page<Notification> getNotificationsByUser(User user, Pageable pageable) {
		return notificationRepository.findByUserIdOrderByCreatedAtDesc(user, pageable);
	}

	@Override
	public List<Notification> getUnreadNotifications(User user) {
		return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(user);
	}

	@Override
	public long getUnreadCount(User user) {
		return notificationRepository.countByUserIdAndReadFalse(user);
	}

	@Override
	public Notification markAsRead(String notificationId, User user) {
		Notification notification = notificationRepository.findById(notificationId)
				   .orElseThrow(() -> new JobPortalException("Notification not found"));
		
		// Verify ownership
		if (!notification.getUserId().getId().equals(user.getId())) {
			   throw new JobPortalException("You don't have permission to access this notification");
		}
		
		notification.setRead(true);
		return notificationRepository.save(notification);
	}

	@Override
	public void markAllAsRead(User user) {
		List<Notification> unread = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(user);
		unread.forEach(n -> {
			n.setRead(true);
			notificationRepository.save(n);
		});
	}

	@Override
	public void deleteNotification(String notificationId, User user) {
		Notification notification = notificationRepository.findById(notificationId)
				   .orElseThrow(() -> new JobPortalException("Notification not found"));
		
		// Verify ownership
		if (!notification.getUserId().getId().equals(user.getId())) {
			   throw new JobPortalException("You don't have permission to delete this notification");
		}
		
		notificationRepository.delete(notification);
	}
}

