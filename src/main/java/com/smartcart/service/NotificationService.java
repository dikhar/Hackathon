package com.smartcart.service;

import com.smartcart.entity.Notification;
import com.smartcart.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Processes pending notifications that are due to be sent
     */
    public void processPendingNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> pendingNotifications = notificationRepository.findPendingNotificationsDueNow(now);
        
        for (Notification notification : pendingNotifications) {
            try {
                sendNotification(notification);
                notification.markAsSent();
                notificationRepository.save(notification);
            } catch (Exception e) {
                notification.markAsFailed("Failed to send: " + e.getMessage());
                notificationRepository.save(notification);
            }
        }
    }
    
    /**
     * Sends a notification based on its channel
     */
    public void sendNotification(Notification notification) {
        switch (notification.getChannel()) {
            case EMAIL:
                sendEmailNotification(notification);
                break;
            case PUSH:
                sendPushNotification(notification);
                break;
            case SMS:
                sendSmsNotification(notification);
                break;
            case IN_APP:
                sendInAppNotification(notification);
                break;
            default:
                throw new IllegalArgumentException("Unsupported notification channel: " + notification.getChannel());
        }
    }
    
    /**
     * Sends email notification
     */
    private void sendEmailNotification(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getUser().getEmail());
            message.setSubject(notification.getTitle());
            message.setText(notification.getMessage());
            message.setFrom("noreply@smartcart.com");
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
    
    /**
     * Sends push notification (placeholder implementation)
     */
    private void sendPushNotification(Notification notification) {
        // Placeholder for push notification implementation
        // In a real application, this would integrate with services like Firebase Cloud Messaging
        System.out.println("Sending push notification to user " + notification.getUser().getId() + 
                          ": " + notification.getTitle());
    }
    
    /**
     * Sends SMS notification (placeholder implementation)
     */
    private void sendSmsNotification(Notification notification) {
        // Placeholder for SMS implementation
        // In a real application, this would integrate with services like Twilio
        System.out.println("Sending SMS to user " + notification.getUser().getId() + 
                          ": " + notification.getMessage());
    }
    
    /**
     * Sends in-app notification (placeholder implementation)
     */
    private void sendInAppNotification(Notification notification) {
        // Placeholder for in-app notification
        // This would typically update a user's notification inbox or send via WebSocket
        System.out.println("Sending in-app notification to user " + notification.getUser().getId() + 
                          ": " + notification.getTitle());
    }
    
    /**
     * Retries failed notifications
     */
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findFailedNotificationsForRetry();
        
        for (Notification notification : failedNotifications) {
            if (notification.canRetry()) {
                try {
                    sendNotification(notification);
                    notification.markAsSent();
                    notificationRepository.save(notification);
                } catch (Exception e) {
                    notification.markAsFailed("Retry failed: " + e.getMessage());
                    notificationRepository.save(notification);
                }
            }
        }
    }
    
    /**
     * Marks notification as opened (for tracking)
     */
    public void markNotificationOpened(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.markAsOpened();
        notificationRepository.save(notification);
    }
    
    /**
     * Marks notification as clicked (for tracking)
     */
    public void markNotificationClicked(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.markAsClicked();
        notificationRepository.save(notification);
    }
    
    /**
     * Creates an immediate notification (bypassing scheduling)
     */
    public void sendImmediateNotification(Notification notification) {
        try {
            sendNotification(notification);
            notification.markAsSent();
            notificationRepository.save(notification);
        } catch (Exception e) {
            notification.markAsFailed("Failed to send immediate notification: " + e.getMessage());
            notificationRepository.save(notification);
            throw new RuntimeException("Failed to send immediate notification", e);
        }
    }
} 