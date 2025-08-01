package com.smartcart.service;

import com.smartcart.entity.Cart;
import com.smartcart.entity.Notification;
import com.smartcart.entity.User;
import com.smartcart.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class SmartReminderService {
    
    @Autowired
    private BehavioralAnalysisService behavioralAnalysisService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Value("${smart-cart.behavioral-analysis.reminder-cooldown-hours:6}")
    private int reminderCooldownHours;
    
    @Value("${smart-cart.notification.max-daily-reminders:3}")
    private int maxDailyReminders;
    
    /**
     * Creates smart reminders for abandoned carts
     */
    public void createSmartReminders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(1); // Carts inactive for 1+ hour
        List<Cart> potentiallyAbandonedCarts = cartRepository.findAbandonedCarts(cutoffTime);
        
        for (Cart cart : potentiallyAbandonedCarts) {
            processCartForReminder(cart);
        }
    }
    
    /**
     * Processes a single cart to determine if reminder should be sent
     */
    public void processCartForReminder(Cart cart) {
        User user = cart.getUser();
        
        // Check if user should receive reminder based on behavioral analysis
        if (!behavioralAnalysisService.shouldSendReminder(user, cart)) {
            return;
        }
        
        // Check daily reminder limit
        if (hasReachedDailyReminderLimit(user)) {
            return;
        }
        
        // Check if we've sent a reminder for this cart recently
        if (hasRecentReminder(cart)) {
            return;
        }
        
        // Create personalized reminder
        createPersonalizedReminder(user, cart);
    }
    
    /**
     * Creates a personalized reminder based on user behavior and preferences
     */
    public void createPersonalizedReminder(User user, Cart cart) {
        // Determine optimal time to send reminder
        LocalTime optimalTime = behavioralAnalysisService.predictOptimalReminderTime(user);
        LocalDateTime scheduledTime = calculateScheduledTime(optimalTime);
        
        // Generate personalized message
        String message = generatePersonalizedMessage(user, cart);
        String title = generatePersonalizedTitle(user, cart);
        
        // Create notification
        Notification notification = new Notification(
            user, 
            cart, 
            Notification.NotificationType.CART_REMINDER,
            determineOptimalChannel(user),
            title,
            message
        );
        notification.setScheduledAt(scheduledTime);
        
        notificationRepository.save(notification);
        
        // Record this as a behavioral pattern
        behavioralAnalysisService.recordUserActivity(user, 
            com.smartcart.entity.BehavioralPattern.PatternType.CHECKOUT_ABANDONMENT);
    }
    
    /**
     * Generates personalized message based on cart contents and user behavior
     */
    private String generatePersonalizedMessage(User user, Cart cart) {
        StringBuilder message = new StringBuilder();
        
        message.append("Hi ").append(user.getName()).append("! ");
        
        // Customize based on cart value
        if (cart.getTotalAmount().doubleValue() > 100) {
            message.append("You have some great items waiting in your cart worth $")
                   .append(cart.getTotalAmount()).append(". ");
        } else {
            message.append("Don't forget about the ").append(cart.getItemCount())
                   .append(" item").append(cart.getItemCount() > 1 ? "s" : "")
                   .append(" in your cart. ");
        }
        
        // Add urgency based on user patterns
        double frequencyScore = behavioralAnalysisService.getShoppingFrequencyScore(user);
        if (frequencyScore > 0.7) {
            message.append("Complete your purchase before items sell out!");
        } else {
            message.append("Complete your purchase at your convenience.");
        }
        
        return message.toString();
    }
    
    /**
     * Generates personalized title for notification
     */
    private String generatePersonalizedTitle(User user, Cart cart) {
        if (cart.getItemCount() == 1) {
            return "Your item is waiting!";
        } else if (cart.getTotalAmount().doubleValue() > 50) {
            return "Complete your $" + cart.getTotalAmount() + " purchase";
        } else {
            return "Your cart is waiting for you";
        }
    }
    
    /**
     * Determines optimal notification channel based on user behavior
     */
    private Notification.NotificationChannel determineOptimalChannel(User user) {
        // For now, default to email. Could be enhanced with user preference analysis
        return Notification.NotificationChannel.EMAIL;
    }
    
    /**
     * Calculates when to schedule the reminder based on optimal time
     */
    private LocalDateTime calculateScheduledTime(LocalTime optimalTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledTime = now.toLocalDate().atTime(optimalTime);
        
        // If the optimal time has passed today, schedule for tomorrow
        if (scheduledTime.isBefore(now.plusMinutes(30))) {
            scheduledTime = scheduledTime.plusDays(1);
        }
        
        return scheduledTime;
    }
    
    /**
     * Checks if user has reached daily reminder limit
     */
    private boolean hasReachedDailyReminderLimit(User user) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        Long todayCount = notificationRepository.countNotificationsSentToUserSince(user, startOfDay);
        
        int userLimit = user.getMaxDailyReminders() != null ? 
            user.getMaxDailyReminders() : maxDailyReminders;
        
        return todayCount >= userLimit;
    }
    
    /**
     * Checks if there's a recent reminder for this cart
     */
    private boolean hasRecentReminder(Cart cart) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(reminderCooldownHours);
        List<Notification> recentReminders = notificationRepository
            .findRecentNotificationsByCartAndType(cart, Notification.NotificationType.CART_REMINDER, cutoff);
        
        return !recentReminders.isEmpty();
    }
    
    /**
     * Creates follow-up reminders for users with strong patterns
     */
    public void createFollowUpReminders() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        List<Cart> recentlyAbandoned = cartRepository.findAbandonedCarts(cutoff);
        
        for (Cart cart : recentlyAbandoned) {
            User user = cart.getUser();
            
            // Only send follow-up to users with high engagement patterns
            double frequencyScore = behavioralAnalysisService.getShoppingFrequencyScore(user);
            if (frequencyScore > 0.6 && !hasReachedDailyReminderLimit(user)) {
                createFollowUpReminder(user, cart);
            }
        }
    }
    
    /**
     * Creates a follow-up reminder with different messaging
     */
    private void createFollowUpReminder(User user, Cart cart) {
        String title = "Last chance! Your cart expires soon";
        String message = String.format(
            "Hi %s! Your cart with %d item%s (worth $%.2f) will expire soon. " +
            "Don't miss out on these great items!",
            user.getName(),
            cart.getItemCount(),
            cart.getItemCount() > 1 ? "s" : "",
            cart.getTotalAmount().doubleValue()
        );
        
        Notification notification = new Notification(
            user,
            cart,
            Notification.NotificationType.CART_REMINDER,
            Notification.NotificationChannel.EMAIL,
            title,
            message
        );
        
        // Schedule within next hour for urgency
        notification.setScheduledAt(LocalDateTime.now().plusMinutes(30));
        notificationRepository.save(notification);
    }
} 