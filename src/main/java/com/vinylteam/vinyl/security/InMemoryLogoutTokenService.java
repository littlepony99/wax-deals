package com.vinylteam.vinyl.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.vinylteam.vinyl.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InMemoryLogoutTokenService implements LogoutTokenStorageService {

    private Cache<String, LocalDateTime> logoutTokensCache;

    private JwtService jwtService;

    @Autowired
    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostConstruct
    public void initCache() {
        logoutTokensCache = Caffeine.newBuilder()
                .expireAfter(getExpirationStrategy())
                .build();
    }

    private Expiry<String, LocalDateTime> getExpirationStrategy() {
        return new Expiry<String, LocalDateTime>() {

            private ZoneOffset offset = ZoneOffset.ofTotalSeconds(0);

            @Override
            public long expireAfterCreate(@NonNull String s, @NonNull LocalDateTime localDateTime, long currentTime) {
                return getDifference(localDateTime);
            }

            @Override
            public long expireAfterUpdate(@NonNull String s, @NonNull LocalDateTime localDateTime, long currentTime, @NonNegative long currentDuration) {
                return currentDuration;
            }

            @Override
            public long expireAfterRead(@NonNull String s, @NonNull LocalDateTime localDateTime, long currentTime, @NonNegative long currentDuration) {
                return currentDuration;
            }

            private long getDifference(@NonNull LocalDateTime localDateTime) {
                return TimeUnit.SECONDS.toNanos(1_000_000_000L * (localDateTime.toEpochSecond(offset) - LocalDateTime.now().toEpochSecond(offset)));
            }
        };
    }

    @Override
    public void storeToken(String token) {
        logoutTokensCache.put(token, jwtService.getExpirationDate(token));
    }

    @Override
    public boolean isTokenBlocked(String token) {
        return logoutTokensCache.asMap().containsKey(token);
    }
}
