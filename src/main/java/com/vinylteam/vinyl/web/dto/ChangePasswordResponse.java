package com.vinylteam.vinyl.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangePasswordResponse {

    private final String message;
    private final String jwtToken;
    private String refreshToken;

}
