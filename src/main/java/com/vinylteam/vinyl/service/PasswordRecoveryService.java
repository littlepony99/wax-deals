package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.web.dto.UserInfoRequest;

import java.util.UUID;

public interface PasswordRecoveryService {

    void changePassword(UserInfoRequest userInfoRequest);

    void sendLink(String email);

    void checkToken(String token);

    UUID generateToken();
}
