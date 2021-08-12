package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class LogoutService implements LogoutHandler {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private InMemoryLogoutTokenService logoutStorageService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = jwtService.extractToken(request);
        if (jwtService.isTokenValid(token)) {
            logoutStorageService.storeToken(token);
        }
    }

}
