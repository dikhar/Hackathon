package com.smartcart.service;

// import com.twilio.Twilio;
// import com.twilio.rest.api.v2010.account.Message;
// import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
//    @Value("${twilio.accountSid}")
//    private String accountSid;
//    @Value("${twilio.authToken}")
//    private String authToken;
//    @Value("${twilio.fromNumber}")
//    private String fromNumber;

    // @PostConstruct
    // public void init() {
    //     Twilio.init(accountSid, authToken);
    // }

    public void sendSms(String to, String message) {
        // Uncomment and use Twilio for real SMS
        // Message.creator(
        //     new PhoneNumber(to),
        //     new PhoneNumber(fromNumber),
        //     message
        // ).create();
        System.out.println("SMS to " + to + ": " + message);
    }
}
