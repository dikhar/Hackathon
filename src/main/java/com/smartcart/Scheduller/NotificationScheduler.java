package com.smartcart.Scheduller;

import com.smartcart.entity.Cart;
import com.smartcart.repository.ShoppingCartRepo;
import com.smartcart.service.AiRecommendationService;
import com.smartcart.service.EmailService;
import com.smartcart.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class NotificationScheduler {
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private ShoppingCartRepo shoppingCartRepo;
    @Autowired
    private AiRecommendationService aiRecommendationService;

    @Scheduled(cron = "0 0 19 * * *") // Every day at 7pm
    public void sendSmartCartReminders() throws Exception {
        List<Cart> carts = shoppingCartRepo.findAll();
        for (Cart cart : carts) {
            if (aiRecommendationService.shouldSendReminder(cart)) {
                String mobile = cart.getUserDetails().getMobileNumber();
                String email = cart.getUserDetails().getEmail();
                String message = aiRecommendationService.generateReminderMessage(cart);
                if (aiRecommendationService.preferredChannel(cart).equals("SMS")) {
                    smsService.sendSms(mobile, message);
                } else {
                    emailService.sendEmail(email, "Complete your purchase!", message);
                }
            }
        }
    }
}
