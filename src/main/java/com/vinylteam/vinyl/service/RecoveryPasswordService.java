package com.vinylteam.vinyl.service;

public interface RecoveryPasswordService {

    void changePassword(String newPassword, String confirmPassword, String token);

    void sendLink(String email);

    void checkToken(String token);

}
