package com.vinylteam.vinyl.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;

public interface JwtService {

    boolean validateToken(String token);

    String extractToken(HttpServletRequest request);

    String createToken(String userEmail, Collection<? extends GrantedAuthority> authorities);

    Authentication getAuthentication(String token);

    LocalDateTime getExpirationDate(String token);
}
