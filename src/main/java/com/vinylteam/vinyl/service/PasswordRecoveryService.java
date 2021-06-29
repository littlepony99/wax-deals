package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;

public interface PasswordRecoveryService {

    void changePassword(UserChangeProfileInfoRequest userProfileInfo);

    void sendLink(String email);

    void checkToken(String token);

}
