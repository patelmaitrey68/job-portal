package com.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String userId, String message, String type) {
        Map<String, String> payload = new HashMap<>();
        payload.put("message", message);
        payload.put("type", type);
        
        messagingTemplate.convertAndSendToUser(
            userId,
            "/queue/notifications",
            payload
        );
    }
}
