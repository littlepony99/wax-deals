package com.vinylteam.vinyl.service;

import javax.servlet.http.HttpServletRequest;

public interface JwtService {

    boolean validateToken(String token);

    String extractToken(HttpServletRequest request);

    String createToken(String userEmail);
}
