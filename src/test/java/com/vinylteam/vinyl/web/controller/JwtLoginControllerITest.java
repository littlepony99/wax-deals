package com.vinylteam.vinyl.web.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.web.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class JwtLoginControllerITest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userDao;

    @Autowired
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
                .andExpect(jsonPath("$.message", equalTo("User is not activated yet")))
                .andExpect(jsonPath("$.resultCode", equalTo("1")))
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
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", hasKey("user")))
                .andExpect(jsonPath("$.user", not(empty())))
                .andExpect(jsonPath("$.message", emptyString()))
                .andExpect(jsonPath("$.resultCode", equalTo("0")))
                .andExpect(jsonPath("$.token", not(empty())))
                .andExpect(jsonPath("$.user.email", equalTo(testUserEmail)))
                .andExpect(jsonPath("$.user.role", equalTo("USER")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String jwtToken = JsonPath.read(response, "$.token");
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
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$", hasKey("resultCode")))
                .andExpect(jsonPath("$.resultCode", equalTo("1")));
    }

    @Test
    @DisplayName("Correct Token: Login controller checks token and returns it in response")
    public void checkCorrectToken() throws Exception {
        //prepare
        mockUserWithStatus(true);
        JwtUser jwtUser = userMapper.mapToDto(builtUser);
        String token = jwtService.createToken(jwtUser.getUsername(), jwtUser.getAuthorities());
        //when
        var response = mockMvc.perform(get("/token").header("Authorization", token))
                //then
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", hasKey("token")))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$", hasKey("resultCode")))
                .andExpect(jsonPath("$.user", not(empty())))
                .andExpect(jsonPath("$.user.email", equalTo(testUserEmail)))
                .andExpect(jsonPath("$.user.role", equalTo("USER")))
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.token", equalTo(token)))
                .andExpect(jsonPath("$.message", emptyString()))
                .andExpect(jsonPath("$.resultCode", equalTo("0")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.info("Response for check token request {}", response);
    }

    @Test
    @DisplayName("Incorrect Token: Login controller checks token and does not return it in response")
    public void checkIncorrectToken() throws Exception {
        mockUserWithStatus(true);
        //prepare
        JwtUser jwtUser = userMapper.mapToDto(builtUser);
        String token = jwtService.createToken(jwtUser.getUsername(), jwtUser.getAuthorities());
        token = token.replace(token.substring(12,15),"");
        //when
        mockMvc.perform(get("/token").header("Authorization", token))
                //then
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", not(hasKey("token"))))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$", hasKey("resultCode")))
                .andExpect(jsonPath("$.message", not(emptyString())))
                .andExpect(jsonPath("$.resultCode", equalTo("1")));
    }

}