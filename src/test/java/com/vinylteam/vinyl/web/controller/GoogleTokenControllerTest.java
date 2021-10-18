package com.vinylteam.vinyl.web.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ExternalUserService;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.web.dto.UserDto;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class GoogleTokenControllerTest {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    private final String testUserEmail = "testuser2@gmail.com";
    private final String testUserPassword = "password";
    private User builtUser;

    public void mockUserWithStatus(boolean status) throws GeneralSecurityException, IOException {
        builtUser = User
                .builder()
                .role(Role.USER)
                .email(testUserEmail)
                .password(encoder.encode(testUserPassword))
                .status(status)
                .build();
        when(userDao.findByEmail(testUserEmail)).thenReturn(Optional.of(builtUser));
        /*GoogleIdToken googleIdToken = new GoogleIdToken(new GoogleIdToken.Payload());
        when(externalUserService.verifyToken(ArgumentMatchers.any())).thenReturn(googleIdToken)*/
    }

    //TODO: fix issue with inability to put google.client.id in discreet property file
    /* Can't put property google.client.id in test/application-dev.properties, it doesn't get parsed.
    google.client.id in test/application.properties is invalid but at least allows to run tests.
     */
    @Test
    @DisplayName("Login by Google test")
    void loginByGoogle() throws Exception {
        //prepare
        mockUserWithStatus(true);
        UserInfoRequest request = UserInfoRequest.builder()
                .token("eyJhbGciOiJSUzI1NiIsImtpZCI6IjhkOTI5YzYzZmYxMDgyYmJiOGM5OWY5OTRmYTNmZjRhZGFkYTJkMTEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiNDkwNDQ0NDc5NjQ2LTlvYXFhOWtocWppYTZrM2dvcTJqdWttdTQwNXBuYjl1LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiNDkwNDQ0NDc5NjQ2LTlvYXFhOWtocWppYTZrM2dvcTJqdWttdTQwNXBuYjl1LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA2MDU0ODAwNjE5MjMzMzUzNDg4IiwiZW1haWwiOiJ0YXJhcy5vcmxvdnNreWlAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJtaHhJd0NHVkNwMWkzNmFrVUVwVElRIiwibmFtZSI6ItCi0LDRgNCw0YEg0J7RgNC70L7QstGB0LrQuNC5IiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BT2gxNEdoRURSN0lMbVFCbm1MOGRFOTE0LWM1UVFHQUVzWktIOEtaNVZQWT1zOTYtYyIsImdpdmVuX25hbWUiOiLQotCw0YDQsNGBIiwiZmFtaWx5X25hbWUiOiLQntGA0LvQvtCy0YHQutC40LkiLCJsb2NhbGUiOiJydSIsImlhdCI6MTYzMjczODM4MCwiZXhwIjoxNjMyNzQxOTgwLCJqdGkiOiIxNDk4Nzk0MzAxNTAzMjVmY2M2NDliNzEyNzFhNzIxNzIyYTI3NGE2In0.PbUgm0dmQgtuvtZv9AfM8uuZRgsQ8637r0TqqGmJAQD1xIj2ERxFEBkqHDHzJpdlL-tEFvMBf_AC9w1AnjboJcXa6ztwyO4RPRPj0DUO3oC34E9OJbRlpHv4djHgXiAWTsUvK0rnlYjPfOmK90uwcACGT0jBJnYgsN8ZxBjfslx_Up6pSa4Iiqm0sTV480dESVRprRZdLSZDQoYwqE-LV_O7xLF3XbvG56A9AshnHwkSoRAepgOFbcGcEQLV2NQUTFBVXBVFAE4zWqPGyGS_W7XKr_ZzrjChj86T3LKfP1NrwBsNNxFeHLGp2iuI_eqguo5zm0rDgurXz-psM4Fqsg")
                .build();
        String json = new ObjectMapper().writeValueAsString(request);
        //when
        String response = mockMvc.perform(post("/google/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$", hasKey("user")))
                .andExpect(jsonPath("$.user", not(empty())))
//                .andExpect(jsonPath("$.message", emptyString()))
                .andExpect(jsonPath("$.jwtToken", not(empty())))
                .andExpect(jsonPath("$.refreshToken", not(empty())))
                .andExpect(jsonPath("$.user.email", not(empty())))
                .andExpect(jsonPath("$.user.role", equalTo("USER")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String jwtToken = JsonPath.read(response, "$.jwtToken");
        DocumentContext context = JsonPath.parse(response);
        UserDto responseUser = context.read("$['user']", UserDto.class);
        assertTrue(jwtService.isTokenValid(jwtToken));
        //assertEquals(testUserEmail, responseUser.getEmail());
        assertEquals(Role.USER, responseUser.getRole());
    }

}