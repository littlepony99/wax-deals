package com.vinylteam.vinyl.service;

import org.springframework.stereotype.Service;

@Service
public class ConstantPasswordGenerator implements PasswordGenerator {
    @Override
    public String generatePassword() {
        return "New3Pass4word123";
    }
}
