package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtService jwtService;
    private final InMemoryLogoutTokenService logoutStorageService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = jwtService.extractToken(request);
        if (jwtService.isTokenValid(token)) {
            logoutStorageService.storeToken(token, jwtService.getExpirationDate(token));
        }
    }

}
