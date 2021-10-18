package com.vinylteam.vinyl.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vinylteam.vinyl.entity.TokenPair;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSecurityResponse {

    private String token;
    private String jwtToken;
    private String refreshToken;
    private UserDto user;
    private String message;

    public void setTokenPair(TokenPair tokenPair) {
        setJwtToken(tokenPair.getJwtToken());
        setRefreshToken(tokenPair.getRefreshToken());
    }
}
