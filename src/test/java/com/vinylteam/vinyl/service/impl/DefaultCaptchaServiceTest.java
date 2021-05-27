package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.service.CaptchaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultCaptchaServiceTest {
    private final CaptchaService captchaService = new DefaultCaptchaService();

    @Test
    @DisplayName("Checks that service generate not empty array for valid session id")
    void nonEmptyArrayForValidSessionIdTest() {
        //prepare
        String id = "sessionId";
        //when
        byte[] captcha = captchaService.getCaptcha(id);
        //then
        Assertions.assertNotNull(captcha);
    }

    @Test
    @DisplayName("Checks that service generate not empty array for empty session id")
    void emptyArrayForEmptySessionIdTest() {
        //prepare
        String id = "";
        //when
        byte[] captcha = captchaService.getCaptcha(id);
        //then
        Assertions.assertNotNull(captcha);
    }

}