package com.smartcart.controller;

import com.smartcart.service.EmailService;
import com.smartcart.service.SmsService;
import com.smartcart.service.SmsServiceFast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendSms() {
        try {
            //smsService.sendSms("+919305583007", "Hi Good Morning! This is your daily reminder from SmartCart.");
            emailService.sendEmail();
            return ResponseEntity.ok("SMS sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send SMS: " + e.getMessage());
        }
    }
}