package com.vinylteam.vinyl.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSecurityResponse {

    String token;
    UserDto user;
    String message;

}
