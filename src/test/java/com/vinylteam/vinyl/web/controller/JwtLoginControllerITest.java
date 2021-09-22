package com.vinylteam.vinyl.web.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.InMemoryLogoutTokenService;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.web.dto.UserDto;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class JwtLoginControllerITest {

    @Autowired
    private InMemoryLogoutTokenService logoutStorageService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userDao;

    //@Autowired
    @SpyBean
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder encoder;

    private final String testUserEmail = "testuser2@gmail.com";
    private final String testUserPassword = "password";
    private User builtUser;

    public void mockUserWithStatus(boolean status) {
        builtUser = User
                .builder()
                .role(Role.USER)
                .email(testUserEmail)
                .password(encoder.encode(testUserPassword))
                .status(status)
                .build();
        when(userDao.findByEmail(testUserEmail)).thenReturn(Optional.of(builtUser));
    }

    @Test
    @DisplayName("Correct Credentials but non-active user: login controller returns error")
    public void loginTestWithNonActivatedUser() throws Exception {
        //prepare
        mockUserWithStatus(false);
        var loginRequest = Map.of(
                "email", testUserEmail,
                "password", testUserPassword);
        String json = new ObjectMapper().writeValueAsString(loginRequest);
        //when
        String response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", not(hasKey("user"))))
                .andExpect(jsonPath("$", not(hasKey("token"))))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.message", equalTo("Your email isn't confirmed. Check your mailbox for the confirmation link")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.info("Response for login of non-activated user {}", response);
    }

    @Test
    @DisplayName("Correct Credentials: login controller returns valid JWT")
    public void loginTest() throws Exception {
        //prepare
        mockUserWithStatus(true);
        var loginRequest = Map.of(
                "email", testUserEmail,
                "password", testUserPassword);
        String json = new ObjectMapper().writeValueAsString(loginRequest);
        //when
        String response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", hasKey("user")))
                .andExpect(jsonPath("$.user", not(empty())))
                .andExpect(jsonPath("$.message", emptyString()))
                .andExpect(jsonPath("$.jwtToken", not(empty())))
                .andExpect(jsonPath("$.refreshToken", not(empty())))
                .andExpect(jsonPath("$.user.email", equalTo(testUserEmail)))
                .andExpect(jsonPath("$.user.role", equalTo("USER")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String jwtToken = JsonPath.read(response, "$.jwtToken");
        DocumentContext context = JsonPath.parse(response);
        UserDto responseUser = context.read("$['user']", UserDto.class);
        assertTrue(jwtService.isTokenValid(jwtToken));
        assertEquals(testUserEmail, responseUser.getEmail());
        assertEquals(Role.USER, responseUser.getRole());
    }

    @Test
    @DisplayName("Bad Credentials: Login controller returns appropriate response")
    public void badCredentialsLoginTest() throws Exception {
        //prepare
        var loginRequest = Map.of(
                "email", testUserEmail + "2",
                "password", testUserPassword);
        String json = new ObjectMapper().writeValueAsString(loginRequest);
        //when
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", not(hasKey("user"))))
                .andExpect(jsonPath("$", not(hasKey("token"))))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message", not(emptyString())));
    }

    @Test
    @DisplayName("Correct Token: Login controller checks token and returns it in response")
    public void checkCorrectToken() throws Exception {
        //prepare
        mockUserWithStatus(true);
        JwtUser jwtUser = userMapper.mapToDto(builtUser);
        String token = jwtService.createAccessToken(jwtUser, UUID.randomUUID().toString());
        //when
        var response = mockMvc.perform(get("/token").header("Authorization", token))
                //then
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", hasKey("token")))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.user", not(empty())))
                .andExpect(jsonPath("$.user.email", equalTo(testUserEmail)))
                .andExpect(jsonPath("$.user.role", equalTo("USER")))
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.token", equalTo(token)))
                .andExpect(jsonPath("$.message", emptyString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.info("Response for check token request {}", response);
    }

    @Test
    @DisplayName("Correct Refresh Token: Login controller takes refresh token and returns new token pair in response")
    public void checkCorrectRefreshToken() throws Exception {
        //prepare
        mockUserWithStatus(true);
        JwtUser jwtUser = userMapper.mapToDto(builtUser);
        var tokenPair = jwtService.getTokenPair(jwtUser);
        var newTokenPair = jwtService.getTokenPair(jwtUser);
        var requestParameters = Map.of("refreshToken", tokenPair.getRefreshToken());
        //when
        Mockito.doReturn(newTokenPair).when(jwtService).getTokenPair(eq(jwtUser));
        String json = new ObjectMapper().writeValueAsString(requestParameters);
        var response = mockMvc.perform(post("/token/refresh-token").header("Authorization", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //then
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", hasKey("jwtToken")))
                .andExpect(jsonPath("$", hasKey("refreshToken")))
                .andExpect(jsonPath("$.user", not(empty())))
                .andExpect(jsonPath("$.user.email", equalTo(testUserEmail)))
                .andExpect(jsonPath("$.user.role", equalTo("USER")))
                .andExpect(jsonPath("$.jwtToken", not(emptyString())))
                .andExpect(jsonPath("$.refreshToken", not(emptyString())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        var objectResponse = new ObjectMapper().readValue(response, UserSecurityResponse.class);
        log.info("Response for refresh token request {}", response);
        assertEquals(newTokenPair.getJwtToken(), objectResponse.getJwtToken());
        assertEquals(newTokenPair.getRefreshToken(), objectResponse.getRefreshToken());
    }

    @Test
    @DisplayName("Used Refresh Token: Login controller takes refresh token and returns Http 4xx code in response")
    public void checkUsedRefreshToken() throws Exception {
        //prepare
        mockUserWithStatus(true);
        JwtUser jwtUser = userMapper.mapToDto(builtUser);
        var tokenPair = jwtService.getTokenPair(jwtUser);
        logoutStorageService.storePairIdentifier(tokenPair.getId(), LocalDateTime.MAX);
        var requestParameters = Map.of("refreshToken", tokenPair.getRefreshToken());
        //when
        String json = new ObjectMapper().writeValueAsString(requestParameters);
        mockMvc.perform(post("/token/refresh-token").header("Authorization", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message", equalTo("Refresh token is expired.")));
    }

    @Test
    @DisplayName("Incorrect Token: Login controller checks token and does not return it in response")
    public void checkIncorrectToken() throws Exception {
        mockUserWithStatus(true);
        //prepare
        JwtUser jwtUser = userMapper.mapToDto(builtUser);
        String token = jwtService.createAccessToken(jwtUser, "122124");
        token = token.replace(token.substring(12, 15), "");
        //when
        mockMvc.perform(get("/token").header("Authorization", token))
                //then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", not(hasKey("token"))))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message", not(emptyString())));
    }

}