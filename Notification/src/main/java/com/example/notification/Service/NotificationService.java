package com.example.notification.Service;

import com.example.notification.Model.Notification;
import com.example.notification.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification createNotification(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        sendNotificationToUser(savedNotification);
        return savedNotification;
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
    private void sendNotificationToUser(Notification notification) {
        String destination = "/topic/notifications/" + notification.getUserId();
        messagingTemplate.convertAndSend(destination, notification);
    }
}
