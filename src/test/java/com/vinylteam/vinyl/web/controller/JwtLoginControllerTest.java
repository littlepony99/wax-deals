package com.vinylteam.vinyl.web.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser2@gmail.com", roles = {"USER"})
class JwtLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userDao;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder encoder;

    @Test
    public void loginTest() throws Exception {
        String testUserEmail = "testuser2@gmail.com";
        String testUserPassword = "password";
        User builtUser = User
                .builder()
                .role(Role.USER)
                .email(testUserEmail)
                .password(encoder.encode(testUserPassword))
                .build();
        when(userDao.findByEmail(testUserEmail)).thenReturn(Optional.of(builtUser));
        var loginRequest = Map.of("email", testUserEmail, "password", testUserPassword);
        String json = new ObjectMapper().writeValueAsString(loginRequest);

        String response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.user", not(empty())))
                .andExpect(jsonPath("$.token", not(empty())))
                .andExpect(jsonPath("$.user.email", equalTo(testUserEmail)))
                .andExpect(jsonPath("$.user.role", equalTo("USER")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String jwtToken = JsonPath.read(response, "$.token");
        DocumentContext context = JsonPath.parse(response);
        User responseUser = context.read("$['user']", User.class);
        assertTrue(jwtService.validateToken(jwtToken));
        assertEquals(testUserEmail, responseUser.getEmail());
        assertTrue(encoder.matches(testUserPassword, responseUser.getPassword()));
        assertEquals(Role.USER, responseUser.getRole());

    }

}