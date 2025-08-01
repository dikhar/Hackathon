package com.smartcart.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartcart.entity.BehavioralPattern;
import com.smartcart.entity.Cart;
import com.smartcart.entity.User;
import com.smartcart.repository.BehavioralPatternRepository;

@Service
@Transactional
public class BehavioralAnalysisService {
    @Autowired
    private BehavioralPatternRepository behavioralPatternRepository;

    /**
     * Records user activity and updates behavioral patterns
     */
    public void recordUserActivity(User user, BehavioralPattern.PatternType activityType) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        int hourOfDay = now.getHour();

        // Find or create pattern for this activity type and time
        Optional<BehavioralPattern> existingPattern = behavioralPatternRepository
                .findByUserAndPatternTypeAndDayOfWeek(user, activityType, dayOfWeek);

        BehavioralPattern pattern;
        if (existingPattern.isPresent()) {
            pattern = existingPattern.get();
            pattern.incrementFrequency();
            pattern.updateLastActivity();

            // Update average hour if pattern is for shopping time
            if (activityType == BehavioralPattern.PatternType.SHOPPING_TIME) {
                updateAverageHour(pattern, hourOfDay);
            }
        } else {
            pattern = new BehavioralPattern(user, activityType);
            pattern.setDayOfWeek(dayOfWeek);
            pattern.setHourOfDay(hourOfDay);
            pattern.updateLastActivity();
        }

        behavioralPatternRepository.save(pattern);
    }

    /**
     * Records cart abandonment behavior
     */
    public void recordCartAbandonment(User user, Cart cart) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        int hourOfDay = now.getHour();

        Optional<BehavioralPattern> pattern = behavioralPatternRepository
                .findByUserAndPatternTypeAndDayOfWeek(user, BehavioralPattern.PatternType.CHECKOUT_ABANDONMENT, dayOfWeek);

        if (pattern.isPresent()) {
            BehavioralPattern abandonmentPattern = pattern.get();
            abandonmentPattern.incrementFrequency();
            abandonmentPattern.updateLastActivity();
            abandonmentPattern.setAverageCartValue(cart.getTotalAmount());
            behavioralPatternRepository.save(abandonmentPattern);
        } else {
            BehavioralPattern newPattern = new BehavioralPattern(user, BehavioralPattern.PatternType.CHECKOUT_ABANDONMENT);
            newPattern.setDayOfWeek(dayOfWeek);
            newPattern.setHourOfDay(hourOfDay);
            newPattern.setAverageCartValue(cart.getTotalAmount());
            newPattern.updateLastActivity();
            behavioralPatternRepository.save(newPattern);
        }
    }

    /**
     * Predicts the optimal time to send a reminder to the user
     */
    public LocalTime predictOptimalReminderTime(User user) {
        // First, check user's preferred time
        if (user.getPreferredReminderTime() != null) {
            return user.getPreferredReminderTime();
        }

        // Look for high-confidence shopping time patterns
        List<BehavioralPattern> shoppingPatterns = behavioralPatternRepository
                .findByUserAndPatternType(user, BehavioralPattern.PatternType.SHOPPING_TIME);

        Optional<BehavioralPattern> highConfidencePattern = shoppingPatterns.stream()
                .filter(BehavioralPattern::isHighConfidence)
                .findFirst();

        if (highConfidencePattern.isPresent()) {
            return highConfidencePattern.get().getOptimalReminderTime();
        }

        // Fall back to general shopping patterns for current day
        DayOfWeek today = LocalDateTime.now().getDayOfWeek();
        Optional<BehavioralPattern> todayPattern = behavioralPatternRepository
                .findByUserAndPatternTypeAndDayOfWeek(user, BehavioralPattern.PatternType.SHOPPING_TIME, today);

        if (todayPattern.isPresent()) {
            return todayPattern.get().getOptimalReminderTime();
        }

        // Default to 6 PM
        return LocalTime.of(18, 0);
    }

    /**
     * Determines if user is likely to abandon cart based on patterns
     */
    public boolean isProbableCartAbandonment(User user, Cart cart) {
        BehavioralPattern abandonmentPatterns = behavioralPatternRepository
                .findByUserAndPatternType(user, BehavioralPattern.PatternType.CHECKOUT_ABANDONMENT);

        // Check if current conditions match abandonment patterns
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek currentDay = now.getDayOfWeek();
        int currentHour = now.getHour();

        return abandonmentPatterns.stream()
                .anyMatch(pattern ->
                        pattern.getDayOfWeek() == currentDay &&
                                pattern.getHourOfDay() != null &&
                                Math.abs(pattern.getHourOfDay() - currentHour) <= 1 &&
                                pattern.isHighConfidence());
    }

    /**
     * Gets user's shopping frequency score
     */
    public double getShoppingFrequencyScore(User user) {
        List<BehavioralPattern> patterns = behavioralPatternRepository.findByUser(user);

        if (patterns.isEmpty()) {
            return 0.1; // Low frequency for new users
        }

        double totalFrequency = patterns.stream()
                .mapToDouble(pattern -> pattern.getActivityFrequency())
                .sum();

        // Normalize to 0-1 scale
        return Math.min(1.0, totalFrequency / 50.0);
    }

    /**
     * Determines optimal notification interval based on user behavior
     */
    public int getOptimalNotificationIntervalHours(User user) {
        double frequencyScore = getShoppingFrequencyScore(user);

        if (frequencyScore > 0.8) {
            return 4; // High frequency users - shorter intervals
        } else if (frequencyScore > 0.5) {
            return 8; // Medium frequency users
        } else {
            return 24; // Low frequency users - longer intervals
        }
    }

    /**
     * Updates average hour for time-based patterns
     */
    private void updateAverageHour(BehavioralPattern pattern, int newHour) {
        if (pattern.getHourOfDay() == null) {
            pattern.setHourOfDay(newHour);
        } else {
            // Simple moving average
            int currentAvg = pattern.getHourOfDay();
            int frequency = pattern.getActivityFrequency();
            int newAvg = (currentAvg * (frequency - 1) + newHour) / frequency;
            pattern.setHourOfDay(newAvg);
        }
    }

    /**
     * Gets behavioral insights for user
     */
    public List<BehavioralPattern> getUserInsights(User user) {
        return behavioralPatternRepository.findHighConfidencePatterns(user, BigDecimal.valueOf(0.5));
    }

    /**
     * Checks if user should receive a reminder based on behavioral patterns
     */
    public boolean shouldSendReminder(User user, Cart cart) {
        // Don't send reminders if user has disabled them
        if (!user.getEnableNotifications()) {
            return false;
        }

        // Check if it's within quiet hours
        LocalTime now = LocalTime.now();
        if (isInQuietHours(user, now)) {
            return false;
        }

        // Check if cart is worth reminding about
        if (cart.isEmpty() || cart.getTotalAmount().compareTo(BigDecimal.valueOf(10)) < 0) {
            return false;
        }

        // Check behavioral patterns
        double abandonmentProbability = isProbableCartAbandonment(user, cart) ? 0.8 : 0.3;
        double frequencyScore = getShoppingFrequencyScore(user);

        // Combine factors to make decision
        double reminderScore = (abandonmentProbability + frequencyScore) / 2;
        return reminderScore > 0.4;
    }

    private boolean isInQuietHours(User user, LocalTime currentTime) {
        LocalTime quietStart = user.getQuietHoursStart();
        LocalTime quietEnd = user.getQuietHoursEnd();

        if (quietStart == null || quietEnd == null) {
            return false;
        }

        if (quietStart.isBefore(quietEnd)) {
            // Same day quiet hours (e.g., 22:00 to 23:59)
            return currentTime.isAfter(quietStart) && currentTime.isBefore(quietEnd);
        } else {
            // Overnight quiet hours (e.g., 22:00 to 08:00)
            return currentTime.isAfter(quietStart) || currentTime.isBefore(quietEnd);
        }
    }

    // Record or update a behavioral pattern
    public void recordPattern(User user, BehavioralPattern.PatternType type, String value) {
        BehavioralPattern pattern = behavioralPatternRepository.findByUserAndPatternType(user, type);
        if (pattern == null) {
            pattern = new BehavioralPattern();
            pattern.setUser(user);
            pattern.setPatternType(type);
        }
        pattern.setValue(value);
        pattern.setLastUpdated(LocalDateTime.now());
        behavioralPatternRepository.save(pattern);
    }

    // Retrieve a behavioral pattern value
    public String getPatternValue(User user, BehavioralPattern.PatternType type) {
        BehavioralPattern pattern = behavioralPatternRepository.findByUserAndPatternType(user, type);
        return pattern != null ? pattern.getValue() : null;
    }

    // Example: increment abandonment count
    public void incrementAbandonmentCount(User user) {
        String val = getPatternValue(user, BehavioralPattern.PatternType.CHECKOUT_ABANDONMENT);
        int count = 0;
        if (val != null) {
            try {
                count = Integer.parseInt(val);
            } catch (NumberFormatException ignored) {
            }
        }
        recordPattern(user, BehavioralPattern.PatternType.CHECKOUT_ABANDONMENT, String.valueOf(count + 1));
    }
} 