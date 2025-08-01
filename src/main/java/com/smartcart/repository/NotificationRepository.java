package com.smartcart.repository;

import com.smartcart.entity.Cart;
import com.smartcart.entity.Notification;
import com.smartcart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUser(User user);
    
    List<Notification> findByUserAndCart(User user, Cart cart);
    
    List<Notification> findByStatus(Notification.NotificationStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.sentAt >= :since")
    List<Notification> findRecentNotificationsByUser(@Param("user") User user, @Param("since") LocalDateTime since);
    
    @Query("SELECT n FROM Notification n WHERE n.cart = :cart AND n.notificationType = :type AND n.sentAt >= :since")
    List<Notification> findRecentNotificationsByCartAndType(
        @Param("cart") Cart cart, 
        @Param("type") Notification.NotificationType type, 
        @Param("since") LocalDateTime since
    );
    
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND n.scheduledAt <= :now")
    List<Notification> findPendingNotificationsDueNow(@Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < 3")
    List<Notification> findFailedNotificationsForRetry();
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.sentAt >= :since")
    Long countNotificationsSentToUserSince(@Param("user") User user, @Param("since") LocalDateTime since);
} 