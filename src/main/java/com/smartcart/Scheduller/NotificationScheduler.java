package com.smartcart.Scheduller;

import com.smartcart.service.EmailService;
import com.smartcart.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Scheduled(cron = "0 */15 * * * *")
    public void sendDailyNotification() {
        // Send Email
        emailService.sendEmail();

        // Send SMS
        //smsService.sendSms();
    }
}
