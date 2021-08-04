package com.vinylteam.vinyl.web.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
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
@WithMockUser(username = "testuser2@gmail.com")
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

    private String testUserEmail = "testuser2@gmail.com";
    ;
    private String testUserPassword = "password";
    private User builtUser;

    @PostConstruct
    public void init() {
        builtUser = User
                .builder()
                .role(Role.USER)
                .email(testUserEmail)
                .password(encoder.encode(testUserPassword))
                .build();
        when(userDao.findByEmail(testUserEmail)).thenReturn(Optional.of(builtUser));
    }

    @Test
    @DisplayName("Correct Credentials: login controller returns valid JWT")
    public void loginTest() throws Exception {
        //prepare
        var loginRequest = Map.of("email", testUserEmail, "password", testUserPassword);
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
        User responseUser = context.read("$['user']", User.class);
        assertTrue(jwtService.validateToken(jwtToken));
        assertEquals(testUserEmail, responseUser.getEmail());
        assertTrue(encoder.matches(testUserPassword, responseUser.getPassword()));
        assertEquals(Role.USER, responseUser.getRole());
    }

    @Test
    @DisplayName("Bad Credentials: Login controller returns appropriate response")
    public void badCredentialsLoginTest() throws Exception {
        //prepare
        var loginRequest = Map.of("email", testUserEmail + "2", "password", testUserPassword);
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
        JwtUser jwtUser = userMapper.mapToDto(builtUser);
        String token = jwtService.createToken(jwtUser.getUsername(), jwtUser.getAuthorities());
        //when
        mockMvc.perform(get("/token").header("Authorization", token))
                //then
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", hasKey("token")))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$", hasKey("resultCode")))
                .andExpect(jsonPath("$.token", not(emptyString())))
                .andExpect(jsonPath("$.token", equalTo(token)))
                .andExpect(jsonPath("$.message", emptyString()))
                .andExpect(jsonPath("$.resultCode", equalTo("0")));
    }

    @Test
    @DisplayName("Incorrect Token: Login controller checks token and does not return it in response")
    public void checkIncorrectToken() throws Exception {
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
