package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.web.dto.UserInfoRequest;

public interface PasswordRecoveryService {

    void changePassword(UserInfoRequest userInfoRequest);

    void sendLink(String email);

    void checkToken(String token);

}
