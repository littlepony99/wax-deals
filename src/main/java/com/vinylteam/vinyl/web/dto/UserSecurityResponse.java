package com.vinylteam.vinyl.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSecurityResponse {
    private String token;
    private UserDto user;
    private String resultCode;
    private String message;
}
