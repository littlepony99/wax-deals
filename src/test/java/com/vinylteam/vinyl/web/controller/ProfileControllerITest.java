package com.vinylteam.vinyl.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.JwtAuthenticationException;
import com.vinylteam.vinyl.service.JwtTokenProvider;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class ProfileControllerITest {

    @Autowired
    private PasswordEncoder encoder;

    @SpyBean
    private UserDao userDao;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;
    private String testUserEmail;

    private final String testUserPassword = "initialPassword12";

    @Autowired
    private Filter jwtValidatorFilter;
    @Autowired
    private JwtTokenProvider jwtService;
    private UserDao userService;

    @PostConstruct
    public void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(jwtValidatorFilter)
                .build();
        String encPassword = encoder.encode(testUserPassword);
        testUserEmail = "user_with_discogs_account@gmail.com";
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
    @WithAnonymousUser
    @DisplayName("Change Discogs user name attribute of anonymous user")
    public void changeDiscogsUserNameForAnonymousUserTest() throws Exception {
        UserInfoRequest userChangeRequest = UserInfoRequest.builder().discogsUserName("discogsUserName").build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        mockMvc.perform(put("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                //then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.resultCode", equalTo("1")))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Access is denied")));
    }

    @Test
    @DisplayName("Change Discogs user name attribute with bad JWT")
    public void changeDiscogsUserTest() throws Exception {
        UserInfoRequest userChangeRequest = UserInfoRequest.builder().discogsUserName("discogsUserName").build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        Exception exception = assertThrows(JwtAuthenticationException.class,
                () -> mockMvc.perform(put("/profile")
                        .header("Authorization", "Bearer_badJWT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)));
    }

    @Test
    @DisplayName("Happy Path: Change Discogs user name attribute")
    public void changeDiscogsUserHappyPathTest() throws Exception {
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));

        String changedDiscogsUserName = "changedDiscogsUserName";
        UserInfoRequest userChangeRequest = UserInfoRequest
                .builder()
                .email(testUserEmail)
                .discogsUserName(changedDiscogsUserName)
                .build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        mockMvc.perform(put("/profile")
                        .header("Authorization", loginResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest));
        User user = userDao.findByEmail(testUserEmail).get();
        assertEquals(changedDiscogsUserName, user.getDiscogsUserName());
    }

    @Test
    @DisplayName("Happy Path: Change user password attribute")
    public void changePasswordHappyPathTest() throws Exception {
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));

        String newUserPassword = "discogsUserPassword3267";
        UserInfoRequest userChangeRequest = UserInfoRequest
                .builder()
                .newPassword(newUserPassword)
                .newPasswordConfirmation(newUserPassword)
                .build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        var response = mockMvc.perform(put("/profile/change-password")
                .header("Authorization", loginResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andReturn()
                .getResponse()
                .getContentAsString();
        UserSecurityResponse loginResponseForNewPassword = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, newUserPassword));
        assertTrue(!loginResponseForNewPassword.getToken().isEmpty());
    }

}
