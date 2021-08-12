package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public interface JwtService {

    boolean isTokenValid(String token);

    String extractToken(HttpServletRequest request);

    String createToken(String userEmail, Collection<? extends GrantedAuthority> authorities);

    Authentication getAuthentication(String token);

    LocalDateTime getExpirationDate(String token);

    UserSecurityResponse getCheckResponseIfTokenValid(String token);

    UserSecurityResponse authenticateByRequest(LoginRequest loginRequest);

}
