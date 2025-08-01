package com.smartcart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    // Notification preferences
    @Column(name = "preferred_reminder_time")
    private LocalTime preferredReminderTime;
    
    @Column(name = "enable_notifications")
    private Boolean enableNotifications = true;
    
    @Column(name = "max_daily_reminders")
    private Integer maxDailyReminders = 3;
    
    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;
    
    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;
    
    // Behavioral pattern preferences
    @Column(name = "timezone")
    private String timezone = "UTC";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cart> carts = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BehavioralPattern> behavioralPatterns = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();
    
    // Constructors
    public User() {}
    
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.preferredReminderTime = LocalTime.of(18, 0); // Default 6 PM
        this.quietHoursStart = LocalTime.of(22, 0); // Default 10 PM
        this.quietHoursEnd = LocalTime.of(8, 0); // Default 8 AM
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public LocalTime getPreferredReminderTime() { return preferredReminderTime; }
    public void setPreferredReminderTime(LocalTime preferredReminderTime) { this.preferredReminderTime = preferredReminderTime; }
    
    public Boolean getEnableNotifications() { return enableNotifications; }
    public void setEnableNotifications(Boolean enableNotifications) { this.enableNotifications = enableNotifications; }
    
    public Integer getMaxDailyReminders() { return maxDailyReminders; }
    public void setMaxDailyReminders(Integer maxDailyReminders) { this.maxDailyReminders = maxDailyReminders; }
    
    public LocalTime getQuietHoursStart() { return quietHoursStart; }
    public void setQuietHoursStart(LocalTime quietHoursStart) { this.quietHoursStart = quietHoursStart; }
    
    public LocalTime getQuietHoursEnd() { return quietHoursEnd; }
    public void setQuietHoursEnd(LocalTime quietHoursEnd) { this.quietHoursEnd = quietHoursEnd; }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Cart> getCarts() { return carts; }
    public void setCarts(List<Cart> carts) { this.carts = carts; }
    
    public List<BehavioralPattern> getBehavioralPatterns() { return behavioralPatterns; }
    public void setBehavioralPatterns(List<BehavioralPattern> behavioralPatterns) { this.behavioralPatterns = behavioralPatterns; }
    
    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }
} 