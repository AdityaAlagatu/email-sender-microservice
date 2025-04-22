package com.emailservice.emailservice.controller;

import com.emailservice.emailservice.model.EmailRequest;
import com.emailservice.emailservice.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sendEmails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping
    public String sendEmails(@RequestBody EmailRequest request) {
        try {
            emailService.sendEmail(request);
            return "Emails sent successfully!";
        } catch (MessagingException e) {
            return "Failed to send emails: " + e.getMessage();
        }
    }
}
