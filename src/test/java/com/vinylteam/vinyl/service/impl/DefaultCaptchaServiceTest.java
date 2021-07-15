package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.CaptchaResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCaptchaServiceTest {
    @InjectMocks
    private DefaultCaptchaService captchaService;
    @Mock
    private RestTemplate testRestTemplate;

    @Test
    @DisplayName("Checks that service generate right response if captcha ok status was received")
    void successResponseFromCaptchaServerTest() {
        //prepare
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setSuccess(true);
        ReflectionTestUtils.setField(captchaService, "recaptchaEndpoint", "endik");
        ReflectionTestUtils.setField(captchaService, "recaptchaSecret", "secret");
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", "secret");
        params.add("response", "captcha");
        when(testRestTemplate.postForObject("endik", params, CaptchaResponse.class)).thenReturn(captchaResponse);
        //when
        boolean result = captchaService.validateCaptcha("captcha");
        //then
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Checks that generate right response if captcha ok status was received")
    void errorResponseFromCaptchaServerTest() {
        //prepare
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setSuccess(false);
        ReflectionTestUtils.setField(captchaService, "recaptchaEndpoint", "endik");
        ReflectionTestUtils.setField(captchaService, "recaptchaSecret", "secret");
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", "secret");
        params.add("response", "captcha");
        when(testRestTemplate.postForObject("endik", params, CaptchaResponse.class)).thenReturn(captchaResponse);
        //when
        boolean result = captchaService.validateCaptcha("captcha");
        //then
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result);
    }

}
