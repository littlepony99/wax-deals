package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.service.EmailConfirmationService;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class RestSignupControllerITest {

    private String testUserEmail = "testuser2@gmail.com";
    private String testUserPassword = "Password123";

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private EmailConfirmationService confirmationTokenDao;

    @Test
    @DisplayName("Failure during registration of user")
    void signUpUserWithFailure() throws Exception {
        UUID token = UUID.randomUUID();
        when(confirmationTokenDao.generateToken()).thenReturn(token);
        var signUpRequest = Map.of("email", testUserEmail,
                "password", testUserPassword,
                "confirmPassword", testUserPassword + "21",
                "discogsUserName", "");
        String json = new ObjectMapper().writeValueAsString(signUpRequest);
        mockMvc.perform(post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.resultCode", not(emptyString())))
                .andExpect(jsonPath("$.resultCode", equalTo("1")));
    }

    @Test
    @DisplayName("Successful registration of user")
    void signUpUser() throws Exception {
        UUID token = UUID.randomUUID();
        log.info("Generated test token: {}", token);
        Mockito.doReturn(token).when(confirmationTokenDao).generateToken();
        var signUpRequest = Map.of("email", testUserEmail,
                "password", testUserPassword,
                "confirmPassword", testUserPassword,
                "discogsUserName", "");
        String json = new ObjectMapper().writeValueAsString(signUpRequest);
        sendAndCheckSignUpRequest(json, testUserEmail);

        var confirmRequest = Map.of(
                "token", token,
                "password", testUserPassword);
        String confirmJson = new ObjectMapper().writeValueAsString(confirmRequest);
        sendAndCheckConfirmationRequest(confirmJson);
    }

    @Test
    @DisplayName("Attempt of registration of existing user")
    void signUpExistingUser() throws Exception {
        UUID token = UUID.randomUUID();
        log.info("Generated test token: {}", token);
        Mockito.doReturn(token).when(confirmationTokenDao).generateToken();
        String testExistingUserEmail = "bill@ms.com";
        String testExistingUserPassword = "Password0987";
        var signUpRequest = Map.of("email", testExistingUserEmail,
                "password", testExistingUserPassword,
                "confirmPassword", testExistingUserPassword,
                "discogsUserName", "");
        String json = new ObjectMapper().writeValueAsString(signUpRequest);
        sendAndCheckSignUpRequest(json, testExistingUserEmail);
        var response = mockMvc.perform(post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.resultCode", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("We can't register user with these credentials, please, check them and try again.")))
                .andExpect(jsonPath("$.resultCode", equalTo("1")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        log.info("Response for signUp request {}", response);

    }

    private void sendAndCheckConfirmationRequest(String confirmJson) throws Exception {
        mockMvc.perform(post("/emailConfirmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(confirmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", hasKey("user")))
                .andExpect(jsonPath("$.user", not(empty())))
                .andExpect(jsonPath("$.message", emptyString()))
                .andExpect(jsonPath("$.resultCode", equalTo("0")))
                .andExpect(jsonPath("$.token", not(empty())))
                .andExpect(jsonPath("$.user.email", equalTo(testUserEmail)))
                .andExpect(jsonPath("$.user.role", equalTo("USER")));
    }

    private void sendAndCheckSignUpRequest(String json, String email) throws Exception {
        var response = mockMvc.perform(post("/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isSeeOther())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.resultCode", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Please confirm your registration. To do this, follow the link that we sent to your email - " + email)))
                .andExpect(jsonPath("$.resultCode", equalTo("0")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        log.info("Response for signUp request {}", response);

    }


}