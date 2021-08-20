package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.exception.entity.UserPostErrors;
import com.vinylteam.vinyl.service.CaptchaService;
import com.vinylteam.vinyl.service.EmailConfirmationService;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
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
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
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
                "message", message,
                "captchaResponse", captcha);
        String json = new ObjectMapper().writeValueAsString(contactUsRequest);
        var response = mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.resultCode", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo(UserPostErrors.INCORRECT_CAPTCHA_ERROR.getMessage())))
                .andExpect(jsonPath("$.resultCode", equalTo("1")));
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
                "message", message,
                "captchaResponse", captcha);
        String json = new ObjectMapper().writeValueAsString(contactUsRequest);
        var response = mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Thank you. We will answer you as soon as possible.")));
    }

}