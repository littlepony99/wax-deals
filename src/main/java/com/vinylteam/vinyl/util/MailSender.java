package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.exception.entity.MailSenderErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailSender {

    private final JavaMailSender emailSender;

    public void sendMail(String recipient, String subject, String mailBody) throws ServerException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("vinyl.project.sender@gmail.com");
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(mailBody);
            emailSender.send(message);
            log.info("Email sent successfully for recipient : {}", recipient);
        } catch (Exception e) {
            log.error("Can't send email to recipient : {}  due to error : {}", recipient, e);
            throw new ServerException(MailSenderErrors.FAILED_TO_SEND_EMAIL.getMessage());
        }
    }

}