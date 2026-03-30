package com.tinhnguyenxanh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String htmlContent) {
        sendEmail(toEmail, subject, htmlContent, null, null);
    }

    public void sendEmail(String toEmail, String subject, String htmlContent,
                          String replyToEmail, String replyToName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@tinhnguyenxanh.vn", "Tình Nguyện Xanh");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            if (replyToEmail != null && !replyToEmail.isBlank()) {
                helper.setReplyTo(replyToEmail, replyToName != null ? replyToName : "Người dùng");
            }

            mailSender.send(message);
        } catch (Exception ex) {
            System.err.println("[EMAIL ERROR] " + ex.getMessage());
            throw new RuntimeException("Không thể gửi email: " + ex.getMessage(), ex);
        }
    }
}
