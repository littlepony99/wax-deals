package com.vinylteam.vinyl.web.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ApiExceptionDto {
    private final String message;
}
