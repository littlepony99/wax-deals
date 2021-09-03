package com.vinylteam.vinyl.web.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ApiExceptionDto {
    private final String message;
}
