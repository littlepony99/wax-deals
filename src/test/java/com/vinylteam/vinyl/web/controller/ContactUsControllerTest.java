package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.exception.entity.MailSenderErrors;
import com.vinylteam.vinyl.exception.entity.UserPostErrors;
import com.vinylteam.vinyl.service.CaptchaService;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.MailSender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class ContactUsControllerTest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserPostService userPostService;
    @SpyBean
    private CaptchaService captchaService;
    @SpyBean
    private UserPostDao userPostDao;
    @SpyBean
    private MailSender mailSender;

    @Test
    @DisplayName("Invalid captcha")
    void contactUsFailedCaptcha() throws Exception {
        String captcha = "INVALID";
        String email = "user@email.com";
        String name = "name";
        String message = "hello";
        Mockito.doReturn(false).when(captchaService).validateCaptcha(captcha);
        var contactUsRequest = Map.of("name", name,
                "email", email,
                "contactUsMessage", message,
                "recaptchaToken", captcha);
        String json = new ObjectMapper().writeValueAsString(contactUsRequest);
        var response = mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo(UserPostErrors.INCORRECT_CAPTCHA_ERROR.getMessage())));
    }

    @Test
    @DisplayName("valid captcha")
    void contactUsPassedCaptcha() throws Exception {
        String captcha = "VALID";
        String email = "user@email.com";
        String name = "name";
        String message = "hello";
        Mockito.doReturn(true).when(captchaService).validateCaptcha(captcha);
        var contactUsRequest = Map.of("name", name,
                "email", email,
                "contactUsMessage", message,
                "recaptchaToken", captcha);
        String json = new ObjectMapper().writeValueAsString(contactUsRequest);
        var response = mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Thank you. We will answer you as soon as possible.")));
    }

    @Test
    @DisplayName("valid captcha")
    void contactUsFailedToSendMessage() throws Exception {
        String captcha = "VALID";
        String email = "user@email.com";
        String name = "name";
        String message = "hello";
        Mockito.doReturn(true).when(captchaService).validateCaptcha(captcha);
        Mockito.doThrow(new ServerException(MailSenderErrors.FAILED_TO_SEND_EMAIL.getMessage())).when(mailSender).sendMail(anyString(), anyString(), anyString());
        var contactUsRequest = Map.of("name", name,
                "email", email,
                "contactUsMessage", message,
                "recaptchaToken", captcha);
        String json = new ObjectMapper().writeValueAsString(contactUsRequest);
        var response = mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo(MailSenderErrors.FAILED_TO_SEND_EMAIL.getMessage())));
    }

}