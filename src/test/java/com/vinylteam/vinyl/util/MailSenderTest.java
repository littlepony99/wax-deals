package com.vinylteam.vinyl.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MailSenderTest {

    @Test
    @DisplayName("Check mail sending with WRONG credentials")
    void sendTestEmailWrongCredentials() {
        String username = "vinyl.project.sender@gmail.com";
        String password = "";
        String auth = "true";
        String host = "smtp.gmail.com";
        String port = "465";

        MailSender mailSender = new MailSender(username, password, host, port, auth);
        boolean result = mailSender.sendMail("alexeysheleg22@gmail.com", "hello, dude", "Test email");
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Check mail sending with RIGHT credentials")
    void sendTestEmail() {
        String username = "vinyl.project.sender@gmail.com";
        String password = "vinyl2021!";
        String auth = "true";
        String host = "smtp.gmail.com";
        String port = "587";

        MailSender mailSender = new MailSender(username, password, host, port, auth);
        boolean result = mailSender.sendMail("SapsanoviyZmey@gmail.com", "hello, dude", "Test email");
        Assertions.assertTrue(result);
    }

}