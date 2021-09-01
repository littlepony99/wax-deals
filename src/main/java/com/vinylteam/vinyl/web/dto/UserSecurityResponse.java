package com.vinylteam.vinyl.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSecurityResponse {

    private String accessToken;
    private String refreshToken;
    private UserDto user;
    private String message;

}
