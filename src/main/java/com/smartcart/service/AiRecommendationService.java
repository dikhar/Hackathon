package com.smartcart.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.smartcart.entity.Cart;
import com.smartcart.entity.UserDetails;
import org.json.JSONObject;
import org.json.JSONArray;

@Service
public class AiRecommendationService {
    private final String hfApiKey;

    public AiRecommendationService(@Value("${huggingface.api-key}") String hfApiKey) {
        this.hfApiKey = hfApiKey;
    }

    // Consider cart abandoned if not updated in last 24 hours
    public boolean shouldSendReminder(Cart cart) {
        if (cart.getLastUpdated() == null) return false;
        long hours = ChronoUnit.HOURS.between(cart.getLastUpdated(), LocalDateTime.now());
        return hours >= 24;
    }

    // Prefer SMS if mobile is present, else email
    public String preferredChannel(Cart cart) {
        UserDetails user = cart.getUserDetails();
        if (user != null && user.getMobileNumber() != null) {
            return "SMS";
        }
        return "EMAIL";
    }

    // Use Hugging Face Inference API to generate a personalized reminder message
    public String generateReminderMessage(Cart cart) {
        String name = cart.getUserDetails() != null ? cart.getUserDetails().getCustomerName() : "there";
        String item = cart.getCartData() != null ? cart.getCartData().getItemName() : "your items";
        String prompt =
            "Write a concise, friendly, and persuasive notification message for a user named '" + name + "' " +
            "who left the item(s) '" + item + "' in their shopping cart. " +
            "The message should:\n" +
            "- Greet the user by name\n" +
            "- Remind them about their cart\n" +
            "- Create a gentle sense of urgency (e.g., items may sell out, limited-time offer, etc.)\n" +
            "- Include a clear call to action to return to the checkout page\n" +
            "- Be positive and supportive\n" +
            "Limit the message to 2-3 sentences, suitable for a push notification or SMS. " +
            "Respond ONLY with the notification message, no explanation or structure.";
        try {
            return callHuggingFace(prompt);
        } catch (Exception e) {
            return "Hi " + name + ", you left items in your cart! Complete your purchase now for a smooth checkout experience.";
        }
    }

    private String callHuggingFace(String prompt) throws Exception {
        URL url = new URL("https://router.huggingface.co/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + hfApiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Build JSON safely
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);

        JSONArray messages = new JSONArray();
        messages.put(message);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "zai-org/GLM-4.5:novita");
        requestBody.put("messages", messages);

        String data = requestBody.toString();

        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        }
        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
        try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {
            String response = scanner.useDelimiter("\\A").next();
            if (responseCode != 200) {
                System.err.println("Hugging Face API error: " + response);
                throw new RuntimeException("Hugging Face API error: " + response);
            }
            // Parse JSON and extract the message content
            JSONObject json = new JSONObject(response);
            JSONArray choices = json.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject msg = choices.getJSONObject(0).getJSONObject("message");
                String content = msg.getString("content");
                return content.trim();
            }
            return response;
        }
    }
} 