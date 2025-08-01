package com.smartcart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smartcart.entity.Cart;
import com.smartcart.service.AiRecommendationService;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiRecommendationService aiRecommendationService;

    @GetMapping("/get")
    public String ai() throws Exception {
        Cart cart = new Cart();
        return aiRecommendationService.generateReminderMessage(cart);
    }
}
