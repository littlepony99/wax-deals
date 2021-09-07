package com.vinylteam.vinyl.security;

import java.time.LocalDateTime;

public interface LogoutTokenStorageService {

    boolean isTokenPairBlocked(String pairIdentifier);

    void storePairIdentifier(String pairIdentifier, LocalDateTime expirationDate);
}
