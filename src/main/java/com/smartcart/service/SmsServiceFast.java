package com.smartcart.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
public class SmsServiceFast {

    @Value("${fast2sms.api.key}")
    private String apiKey;

    @Value("${fast2sms.base.url}")
    private String baseUrl;

    @Value("${fast2sms.sender_id}")
    private String senderId;

    @Value("${fast2sms.language}")
    private String language;

    @Value("${fast2sms.route}")
    private String route;

    @Value("${fast2sms.flash}")
    private String flash;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendSms(String phoneNumber, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("sender_id", senderId);
        requestData.add("message", message);
        requestData.add("language", language);
        requestData.add("route", route);
        requestData.add("numbers", phoneNumber);
        requestData.add("flash", flash);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);
            System.out.println("SMS Sent: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }
}

