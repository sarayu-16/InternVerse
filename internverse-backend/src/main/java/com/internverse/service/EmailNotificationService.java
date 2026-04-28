package com.internverse.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends transactional emails when mail is configured; otherwise logs only.
 */
@Service
@Slf4j
public class EmailNotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${internverse.mail.enabled:false}")
    private boolean mailEnabled;

    public void sendTaskAssigned(String toEmail, String internName, String taskTitle) {
        String subject = "InternVerse: New task assigned — " + taskTitle;
        String body = "Hello " + internName + ",\n\nYou have been assigned a new task: " + taskTitle
                + "\n\nPlease log in to InternVerse to view details and submit your work.\n";
        send(toEmail, subject, body);
    }

    public void sendTaskCompleted(String toEmail, String recipientName, String taskTitle, String internName) {
        String subject = "InternVerse: Task submitted — " + taskTitle;
        String body = "Hello " + recipientName + ",\n\n" + internName + " has submitted work for task: "
                + taskTitle + "\n\nReview it in the admin/mentor dashboard.\n";
        send(toEmail, subject, body);
    }

    public void sendCertificateIssued(String toEmail, String studentName, String verificationCode, String link) {
        String subject = "InternVerse: Your certificate is ready";
        String body = "Congratulations " + studentName + "!\n\nYour certificate has been issued.\n"
                + "Verification code: " + verificationCode + "\n"
                + (link != null ? "Download: " + link + "\n" : "")
                + "\nThank you for completing your internship.\n";
        send(toEmail, subject, body);
    }

    public void sendEmail(String toEmail, String subject, String body) {
        send(toEmail, subject, body);
    }

    private void send(String to, String subject, String text) {
        if (!mailEnabled || fromAddress == null || fromAddress.isBlank()) {
            log.info("[email disabled] To: {} | {} | {}", to, subject, text.replace("\n", " "));
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            log.info("Email sent to: {} | Subject: {}", to, subject);
        } catch (Exception e) {
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
