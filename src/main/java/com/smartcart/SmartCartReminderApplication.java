package com.smartcart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class SmartCartReminderApplication {
    
    public static void main(String[] args) {
        log.debug("Start Application");
        SpringApplication.run(SmartCartReminderApplication.class, args);
    }
} 
