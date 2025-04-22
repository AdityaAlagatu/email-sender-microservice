package com.emailservice.emailservice.service;

import com.emailservice.emailservice.model.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(EmailRequest request) throws MessagingException {
        // 1️⃣ Send Immediately
        sendNow(request);

        // 2️⃣ Schedule for next day at 10 AM
        scheduleEmailForNextMorning(request);
    }

    private void sendNow(EmailRequest request) throws MessagingException {
        for (String to : request.getEmails()) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(request.getSubject());
            helper.setText(request.getMessage());

            FileSystemResource file = new FileSystemResource(new File(request.getResumePath()));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
        }
    }

    private void scheduleEmailForNextMorning(EmailRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next10Am = now.withHour(10).withMinute(0).withSecond(0).withNano(0);

        if (now.compareTo(next10Am) >= 0) {
            next10Am = next10Am.plusDays(1);
        }

        Date scheduleTime = Date.from(next10Am.atZone(ZoneId.systemDefault()).toInstant());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    sendNow(request);  // Reuse same logic
                } catch (MessagingException e) {
                    e.printStackTrace(); // Use proper logger in production
                }
            }
        }, scheduleTime);
    }
}
