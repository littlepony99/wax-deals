package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.TokenPair;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

public interface JwtService {

    boolean isTokenValid(String token);

    boolean isTokenValid(String token, String tokenType);

    String extractToken(HttpServletRequest request);

    String createAccessToken(JwtUser user, String pairIdentifier);

    String createRefreshToken(JwtUser user, String pairIdentifier);

    void tryJwtAuthorization(HttpServletRequest request);

    boolean tryJwtAuthorization(HttpServletRequest request, String token);

    Authentication getAuthentication(String token);

    LocalDateTime getExpirationDate(String token);

    String getPairIdentifier(String token);

    UserSecurityResponse getCheckResponseIfTokenValid(String token);

    UserSecurityResponse authenticateByRequest(LoginRequest loginRequest);

    TokenPair getTokenPair(JwtUser user);

    UserSecurityResponse refreshByToken(String refreshToken);
}
