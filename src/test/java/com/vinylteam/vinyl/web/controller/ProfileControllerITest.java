package com.vinylteam.vinyl.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.JwtAuthenticationException;
import com.vinylteam.vinyl.service.impl.JwtTokenProvider;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    private String testUserPassword = "initialPassword12";
    private final String newUserPassword = "discogsUserPassword3267";

    @Autowired
    private Filter jwtValidatorFilter;
    @Autowired
    private JwtTokenProvider jwtService;
    private UserDao userService;

    @BeforeAll
    void beforeAll() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(jwtValidatorFilter)
                .build();
        String encPassword = encoder.encode(testUserPassword);
        testUserEmail = "user_with_discogs_account@gmail.com";
        createTestUser(testUserEmail, encPassword);
    }

    private void createTestUser(String user, String encPassword) {
        User userToBeCreated = User.builder()
                .status(true)
                .email(user)
                .salt("salt")
                .role(Role.USER)
                .password(encPassword)
                .build();
        userDao.add(userToBeCreated);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Change Discogs user name attribute of anonymous user")
    public void changeDiscogsUserNameForAnonymousUserTest() throws Exception {
        UserInfoRequest userChangeRequest = UserInfoRequest.builder().discogsUserName("discogsUserName").build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        var response = mockMvc.perform(put("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", not(empty())))
                //.andExpect(jsonPath("$.resultCode", equalTo("1")))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Access is denied")))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @Order(1)
    @DisplayName("Empty Email: Change Discogs user name and empty Email attributes")
    public void changeDiscogsUserEmptyEmailTest() throws Exception {
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));
        UserInfoRequest userChangeRequest = UserInfoRequest
                .builder()
                .email("")
                .build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        mockMvc.perform(put("/profile")
                        .header("Authorization", loginResponse.getJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Error. Email is empty. Please enter email correctly.")));
    }

    @Test
    @Order(2)
    @DisplayName("Change Discogs user name attribute with bad JWT")
    public void changeDiscogsUserTest() throws Exception {
        UserInfoRequest userChangeRequest = UserInfoRequest.builder().discogsUserName("discogsUserName").build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        mockMvc.perform(put("/profile")
                .header("Authorization", "Bearer_badJWT")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
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
                        .header("Authorization", loginResponse.getJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$", not(hasKey("token"))))
                .andExpect(jsonPath("$.message", equalTo("Your email and/or discogs username have been changed.")));
        User user = userDao.findByEmail(testUserEmail).get();
        assertEquals(changedDiscogsUserName, user.getDiscogsUserName());
        assertEquals(testUserEmail, user.getEmail());
    }

    @Test
    @Order(4)
    @DisplayName("Happy Path: Change Discogs user name and Email attributes")
    public void changeAllProfileFieldsHappyPathTest() throws Exception {
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));

        String changedDiscogsUserName = "changedDiscogsUserName";
        String newTestUserEmail = testUserEmail + "1";
        UserInfoRequest userChangeRequest = UserInfoRequest
                .builder()
                .email(newTestUserEmail)
                .discogsUserName(changedDiscogsUserName)
                .build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        mockMvc.perform(put("/profile")
                        .header("Authorization", loginResponse.getJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.jwtToken", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Your email and/or discogs username have been changed.")));
        User user = userDao.findByEmail(newTestUserEmail).get();
        assertEquals(changedDiscogsUserName, user.getDiscogsUserName());
        assertEquals(newTestUserEmail, user.getEmail());
        testUserEmail = newTestUserEmail;
    }

    @Test
    @Order(6)
    @DisplayName("Happy Path: Change user password attribute")
    public void changePasswordHappyPathTest() throws Exception {
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));

        UserInfoRequest userChangeRequest = UserInfoRequest
                .builder()
                .password(testUserPassword)
                .newPassword(newUserPassword)
                .newPasswordConfirmation(newUserPassword)
                .build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        var response = mockMvc.perform(put("/profile/change-password")
                        .header("Authorization", loginResponse.getJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andReturn()
                .getResponse()
                .getContentAsString();
        UserSecurityResponse loginResponseForNewPassword = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, newUserPassword));
        assertTrue(!loginResponseForNewPassword.getJwtToken().isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("Happy Path: Change user password attribute with wrong old password")
    public void changePasswordBadOldPasswordTest() throws Exception {
        UserSecurityResponse loginResponse = jwtService.authenticateByRequest(new LoginRequest(testUserEmail, testUserPassword));

        UserInfoRequest userChangeRequest = UserInfoRequest
                .builder()
                .password(testUserPassword + "1")
                .newPassword(newUserPassword)
                .newPasswordConfirmation(newUserPassword)
                .build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(userChangeRequest);
        mockMvc.perform(put("/profile/change-password")
                        .header("Authorization", loginResponse.getJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }
}
