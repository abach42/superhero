package com.abach42.superhero.shared.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JavaMailSender javaMailSender;

    private final String mailFrom;


    private final String mailReplyTo;

    public EmailService(JavaMailSender javaMailSender,
            @Value("${com.abach42.superhero.mail.from}")
            String mailFrom,
            @Value("${com.abach42.superhero.mail.reply-to}")
            String mailReplyTo) {
        this.javaMailSender = javaMailSender;
        this.mailFrom = mailFrom;
        this.mailReplyTo = mailReplyTo;
    }

    public void sendEmail(String to, String subject, String body) {
        send(to, subject, body, null, null);
    }

    public void sendEmailWithAttachment(String to, String subject, String body, String content, String filename) {
        send(to, subject, body, content, filename);
    }

    private void send(String to, String subject, String body, String attachmentContent, String filename) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = createHelper(message);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            if (attachmentContent != null && filename != null) {
                helper.addAttachment(filename, new ByteArrayResource(attachmentContent.getBytes(StandardCharsets.UTF_8)));
            }

            javaMailSender.send(message);
            logger.info("Email successfully sent to {} with subject: {}", to, subject);

        } catch (MessagingException | MailException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private MimeMessageHelper createHelper(MimeMessage message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setFrom(mailFrom);
        helper.setReplyTo(mailReplyTo);
        return helper;
    }
}
