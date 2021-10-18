package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FacebookUserServiceImplTest {

    @Autowired
    private FacebookUserServiceImpl facebookService;

    @Test
    void processExternalAuthorization() throws ServerException, GeneralSecurityException, IOException {
        String facebookAccessToken = "EAA95ZAisgz4ABAOXUDR0ffCOj0q31BUZAGKaI1qtPmmSRg3c58u47BoZCZAX7NsyPm27egWLCiO3SdqZBZAQM5nR2ZCLSi3vXQYOZC1tYKjzSSO6GzmcVG8kIKsHb4676qplFJ5E8KBXP4OkZAtf099Q4B7uKiKxZCW1Qdmz6I8flcYUMG6LiZA4tfg3JlF2GK4TfXAkIJkjSkZByu4tJVqGqHzx";

        var response = facebookService.processExternalAuthorization(facebookAccessToken);
        assertTrue(response.getUser().getEmail().isEmpty());
    }
}