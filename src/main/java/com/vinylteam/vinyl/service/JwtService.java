package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.web.dto.LoginRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public interface JwtService {

    boolean validateToken(String token);

    String extractToken(HttpServletRequest request);

    String createToken(String userEmail, Collection<? extends GrantedAuthority> authorities);

    Authentication getAuthentication(String token);

    LocalDateTime getExpirationDate(String token);

    Map<String, Object> checkTokenAndReturnCredentialsMap(String token);

    Map<String, Object> authenticateAndReturnCredentialsMap(LoginRequest loginRequest);
}
