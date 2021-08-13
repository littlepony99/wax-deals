package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.exception.entity.EmailConfirmationErrors;
import com.vinylteam.vinyl.service.EmailConfirmationService;
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
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class RestSignupControllerITest {

    private String testUserEmail = "testuser2@gmail.com";
    private String testUserPassword = "Password123";
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private EmailConfirmationService emailConfirmationService;
    @SpyBean
    private UserService userService;
    @SpyBean
    private ConfirmationTokenDao confirmationTokenDao;

    @Test
    @DisplayName("Failure during registration of user")
    void signUpUserWithFailure() throws Exception {
        UUID token = UUID.randomUUID();
        when(emailConfirmationService.generateConfirmationToken()).thenReturn(token);
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
        Mockito.doReturn(token).when(emailConfirmationService).generateConfirmationToken();
        var signUpRequest = Map.of("email", testUserEmail,
                "password", testUserPassword,
                "confirmPassword", testUserPassword,
                "discogsUserName", "");
        String json = new ObjectMapper().writeValueAsString(signUpRequest);
        sendAndCheckSignUpRequest(json, testUserEmail);
    }

    @Test
    @DisplayName("Attempt of registration of existing user")
    void signUpExistingUser() throws Exception {
        UUID token = UUID.randomUUID();
        log.info("Generated test token: {}", token);
        Mockito.doReturn(token).when(emailConfirmationService).generateConfirmationToken();
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

    @Test
    @DisplayName("Confirming email with existing token")
    void getConfirmationResponseExistingToken() throws Exception {
        String token = UUID.randomUUID().toString();
        Mockito.doReturn(Optional.of(dataGenerator.getConfirmationTokenWithUserId(1))).when(confirmationTokenDao).findByToken(eq(UUID.fromString(token)));
        Mockito.doReturn(Optional.of(dataGenerator.getUserWithNumber(1))).when(userService).findById(1);
        mockMvc.perform((put("/emailConfirmation"))
                        .param("confirm-token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.resultCode", equalTo("0")))
                .andExpect(jsonPath("$.message", equalTo("your email confirmed. Now you can log in")));
    }

    @Test
    @DisplayName("Confirming email with non-existent in db token")
    void getConfirmationResponseNonExistingToken() throws Exception {
        String token = UUID.randomUUID().toString();
        Mockito.doReturn(Optional.empty()).when(confirmationTokenDao).findByToken(eq(UUID.fromString(token)));
        Mockito.doReturn(Optional.of(dataGenerator.getUserWithNumber(1))).when(userService).findById(1);
        mockMvc.perform((put("/emailConfirmation"))
                .param("confirm-token", token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.resultCode", equalTo("1")))
                .andExpect(jsonPath("$.message", equalTo(EmailConfirmationErrors.TOKEN_FROM_LINK_NOT_FOUND.getMessage())));
    }

    @Test
    @DisplayName("Confirming email with non-existent in db token")
    void getConfirmationResponseNotUUIDToken() throws Exception {
        String token = "not uuid format";
        mockMvc.perform((put("/emailConfirmation"))
                .param("confirm-token", token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.resultCode", equalTo("1")))
                .andExpect(jsonPath("$.message", equalTo(EmailConfirmationErrors.TOKEN_FROM_LINK_NOT_UUID.getMessage())));
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