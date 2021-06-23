package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;

public interface RecoveryPasswordService {

    void changePassword(UserChangeProfileInfoRequest userProfileInfo);

    void sendLink(String email);

    void checkToken(String token);

}
