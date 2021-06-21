package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @MockBean
    private UserService mockedUserService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get profile page")
    void getProfilePage() throws Exception {

        this.mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Profile")));;
    }
}