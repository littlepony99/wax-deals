package com.vinylteam.vinyl.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Service
public class InMemoryLogoutTokenService implements LogoutTokenStorageService {

    private Cache<String, LocalDateTime> logoutTokensCache;

    @PostConstruct
    public void initCache() {
        logoutTokensCache = Caffeine.newBuilder()
                .expireAfter(getExpirationStrategy())
                .build();
    }

    @Override
    public boolean isTokenPairBlocked(String pairIdentifier) {
        return logoutTokensCache.asMap().containsKey(pairIdentifier);
    }

    @Override
    public void storePairIdentifier(String pairIdentifier, LocalDateTime expirationDate) {
        logoutTokensCache.put(pairIdentifier, expirationDate);
    }

    private Expiry<String, LocalDateTime> getExpirationStrategy() {
        return new Expiry<>() {

            private final ZoneOffset offset = ZoneOffset.ofTotalSeconds(0);

            @Override
            public long expireAfterCreate(@NonNull String entryKey, @NonNull LocalDateTime localDateTime, long currentTime) {
                return getDifference(localDateTime);
            }

            @Override
            public long expireAfterUpdate(@NonNull String entryKey, @NonNull LocalDateTime localDateTime, long currentTime, @NonNegative long currentDuration) {
                return currentDuration;
            }

            @Override
            public long expireAfterRead(@NonNull String entryKey, @NonNull LocalDateTime localDateTime, long currentTime, @NonNegative long currentDuration) {
                return currentDuration;
            }

            private long getDifference(@NonNull LocalDateTime localDateTime) {
                return TimeUnit.SECONDS.toNanos(1_000_000_000L * (localDateTime.toEpochSecond(offset) - LocalDateTime.now().toEpochSecond(offset)));
            }
        };
    }
}
