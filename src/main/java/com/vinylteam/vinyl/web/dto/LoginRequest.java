package com.vinylteam.vinyl.web.dto;

import lombok.Value;

@Value
public class LoginRequest {
    String email;
    String password;
}
