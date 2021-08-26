package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.security.impl.SpringSecurityService;
import com.vinylteam.vinyl.service.impl.JwtTokenProvider;
import com.vinylteam.vinyl.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @MockBean
    private UserService mockedUserService;

    @MockBean
    private SpringSecurityService securityService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setMockMvc() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(sharedHttpSession()).build();
    }

    @Test
    @DisplayName("Get profile page")
    void getProfilePage() throws Exception {
        this.mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h2>Profile</h2>")))
                .andReturn();
    }

    @Test
    @DisplayName("Get edit profile page")
    void getEditProfilePage() throws Exception {
        this.mockMvc.perform(get("/profile/edit-profile"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h2>Edit Profile</h2>")))
                .andReturn();
    }

}