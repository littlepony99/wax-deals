package com.vinylteam.vinyl.security;

import com.vinylteam.vinyl.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface SecurityService {

    User createUserWithHashedPassword(String email, char[] password);

    boolean validateIfPasswordMatches(User user, char[] password);

    void validatePassword(String password, String confirmationPassword);

    void validatePassword(String password);

    void emailFormatCheck(String email);

    void setEncoder(PasswordEncoder encoder);

}
