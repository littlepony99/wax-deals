package com.vinylteam.vinyl.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
@Service
public class MailSender {

    private static final String PRODUCTION_ENVIRONMENT = "PROD";
    private final String username;
    private final Session session;

    public MailSender(@Value("${mail.smtp.username}") String username,
                      @Value("${mail.smtp.password}") String password,
                      @Value("${mail.smtp.host}") String host,
                      @Value("${mail.smtp.port}") String port,
                      @Value("${mail.smtp.auth}") String auth) {
        this.username = username;

        Properties properties = new Properties();
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.socketFactory.port", port);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        String env = System.getenv("env");
        if (PRODUCTION_ENVIRONMENT.equals(env)) {
            properties.put("mail.smtps.host", host);
            properties.put("mail.smtps.auth", auth);
        } else {
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.auth", auth);
        }

        session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        PasswordAuthentication passwordAuthentication = new PasswordAuthentication(username, password);
                        return passwordAuthentication;
                    }
                });
    }

    public boolean sendMail(String recipient, String subject, String mailBody) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient)
            );
            message.setSubject(subject);
            message.setText(mailBody);

            Transport.send(message);
            log.info("Email sent successfully for recipient : {}", recipient);

        } catch (Exception e) {
            log.error("Can't send email to recipient : {}  due to error : {}", recipient, e.toString());
            return false;
        }
        return true;
    }

}