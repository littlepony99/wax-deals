package com.vinylteam.vinyl.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public JwtAuthenticationException(String message) {
        super(message);
    }
}
