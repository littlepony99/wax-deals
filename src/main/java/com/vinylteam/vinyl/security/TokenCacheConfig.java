package com.vinylteam.vinyl.security;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class TokenCacheConfig {

    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine
                .newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES);
    }

}
