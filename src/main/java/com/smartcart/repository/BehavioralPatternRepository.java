package com.smartcart.repository;

import com.smartcart.entity.BehavioralPattern;
import com.smartcart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface BehavioralPatternRepository extends JpaRepository<BehavioralPattern, Long> {
    
    List<BehavioralPattern> findByUser(User user);
    
    BehavioralPattern findByUserAndPatternType(User user, BehavioralPattern.PatternType patternType);
    
    Optional<BehavioralPattern> findByUserAndPatternTypeAndDayOfWeek(
        User user, 
        BehavioralPattern.PatternType patternType, 
        DayOfWeek dayOfWeek
    );
    
    @Query("SELECT bp FROM BehavioralPattern bp WHERE bp.user = :user AND bp.patternConfidence >= :minConfidence")
    List<BehavioralPattern> findHighConfidencePatterns(@Param("user") User user, @Param("minConfidence") java.math.BigDecimal minConfidence);
    
    @Query("SELECT bp FROM BehavioralPattern bp WHERE bp.user = :user AND bp.patternType = :patternType AND bp.hourOfDay = :hour")
    Optional<BehavioralPattern> findByUserAndPatternTypeAndHour(
        @Param("user") User user, 
        @Param("patternType") BehavioralPattern.PatternType patternType, 
        @Param("hour") Integer hour
    );
    
    @Query("SELECT bp FROM BehavioralPattern bp WHERE bp.patternType = 'SHOPPING_TIME' AND bp.patternConfidence >= 0.5 ORDER BY bp.patternConfidence DESC")
    List<BehavioralPattern> findReliableShoppingTimePatterns();
} 