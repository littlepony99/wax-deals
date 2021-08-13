package com.vinylteam.vinyl.security;

public interface LogoutTokenStorageService {
    void storeToken(String token);

    boolean isTokenBlocked(String token);
}
