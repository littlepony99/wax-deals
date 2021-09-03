package com.vinylteam.vinyl.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.PasswordRecoveryService;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class PasswordRecoveryControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder encoder;
    @SpyBean
    private PasswordRecoveryService passwordRecoveryService;
    private String testUserEmail;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private MailSender mailSender;

    private final String testUserNewPassword = "New3Pass4word123";
    private final String testUserOldPassword = "initialPassword12";

    public void init() {
        String encPassword = encoder.encode(testUserOldPassword);
        testUserEmail = "recovery_user@gmail.com";
        User user = User.builder()
                .status(true)
                .email(testUserEmail)
                .salt("salt")
                .role(Role.USER)
                .password(encPassword)
                .build();
        userDao.add(user);
    }

    @Test
    @DisplayName("Controller returns error as a response for non-existing user password recovery request")
    public void testPasswordRecoveryRequestNonExistingUser() throws Exception {
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail("IAmNotExisting@google.com");
        String json = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(post("/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("We can't find matching email. Please check your email or contact us.")));
    }

    @Test
    @DisplayName("Checks whether the controller accepts 1st and 2nd request for password recovery")
    public void testPasswordRecoveryRequest() throws Exception {
        //prepare
        init();
        UUID token = UUID.randomUUID();
        UserInfoRequest request = new UserInfoRequest();
        request.setEmail(testUserEmail);
        when(passwordRecoveryService.generateToken()).thenReturn(token);

        doAnswer((i) -> {
            log.info("Sending Email by mock");
            return null;
        }).when(mailSender).sendMail(anyString(), anyString(), anyString());

        String json = new ObjectMapper().writeValueAsString(request);
        //when
        mockMvc.perform(post("/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.message", emptyString()));

        UserInfoRequest stage2Request = UserInfoRequest.builder()
                .newPassword(testUserNewPassword)
                .newPasswordConfirmation(testUserNewPassword)
                .token(token.toString())
                .build();
        String stage2JsonRequest = new ObjectMapper().writeValueAsString(stage2Request);

        mockMvc.perform(put("/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stage2JsonRequest))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.message", emptyString()));

        User user = userDao.findByEmail(testUserEmail).get();
        assertTrue(encoder.matches(testUserNewPassword, user.getPassword()));
    }

    @Test
    @DisplayName("Checks non-existing token")
    public void testCheckToken() throws Exception {
        //prepare
        UUID token = UUID.randomUUID();
        //when
        String recoveryResponse = mockMvc.perform(get("/password-recovery?token=" + token.toString()))
                //then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Your link is incorrect! Please check the link in the your email or contact support.")))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("Checks non-existing token: new endpoint")
    public void testCheckTokenRestEndPoint() throws Exception {
        //prepare
        UUID token = UUID.randomUUID();
        //when
        String recoveryResponse = mockMvc.perform(get("/password-recovery/" + token.toString()))
                //then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Your link is incorrect! Please check the link in the your email or contact support.")))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("Controller rejects 2nd stage recovery request with non-existing token")
    public void testBadRecoveryPasswordRequest() throws Exception {
        //prepare
        UUID token = UUID.randomUUID();
        UserInfoRequest stage2Request = UserInfoRequest.builder()
                .newPassword(testUserNewPassword)
                .newPasswordConfirmation(testUserNewPassword)
                .token(token.toString())
                .build();
        String stage2JsonRequest = new ObjectMapper().writeValueAsString(stage2Request);
        //when
        mockMvc.perform(put("/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stage2JsonRequest))
                //then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Your link is incorrect! Please check the link in the your email or contact support.")));
    }

}