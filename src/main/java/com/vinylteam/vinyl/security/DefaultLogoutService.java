package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class DefaultLogoutService implements LogoutService {

    private final JwtService jwtService;
    private final InMemoryLogoutTokenService logoutStorageService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = jwtService.extractToken(request);
        if (jwtService.isTokenValid(token)) {
            logoutStorageService.storePairIdentifier(jwtService.getPairIdentifier(token), jwtService.getExpirationDate(token));
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        logout(request, null, null);
    }
}
