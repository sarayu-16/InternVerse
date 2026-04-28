package com.internverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;

/**
 * Entry point for the InternVerse Spring Boot application.
 * Mail auto-configuration is excluded until SMTP is configured; see README.
 */
@SpringBootApplication(exclude = {MailSenderAutoConfiguration.class})
public class InternVerseApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternVerseApplication.class, args);
    }
}
