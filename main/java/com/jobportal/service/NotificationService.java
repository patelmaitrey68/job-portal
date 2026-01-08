package com.jobportal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobportal.entity.Notification;
import com.jobportal.entity.User;

public interface NotificationService {
	Notification createNotification(Notification notification);
	Page<Notification> getNotificationsByUser(User user, Pageable pageable);
	List<Notification> getUnreadNotifications(User user);
	long getUnreadCount(User user);
	Notification markAsRead(String notificationId, User user);
	void markAllAsRead(User user);
	void deleteNotification(String notificationId, User user);
}

