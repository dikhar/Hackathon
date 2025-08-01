package com.smartcart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("shivanshv4@gmail.com");
        message.setTo("shivansh.vaish@telekom-digital.com");
        message.setSubject("Daily Reminder");
        message.setText("Good morning! This is your daily reminder.");
        mailSender.send(message);
    }
}

