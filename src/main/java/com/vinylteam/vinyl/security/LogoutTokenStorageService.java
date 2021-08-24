package com.vinylteam.vinyl.security;

import java.time.LocalDateTime;

public interface LogoutTokenStorageService {
    void storeToken(String token, LocalDateTime expirationDate);

    boolean isTokenBlocked(String token);
}
