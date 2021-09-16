package com.vinylteam.vinyl.entity;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenPair {

    String id;
    String jwtToken;
    String refreshToken;
}
