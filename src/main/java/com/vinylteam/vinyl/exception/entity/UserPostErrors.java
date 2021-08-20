package com.vinylteam.vinyl.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserPostErrors {

    INCORRECT_CAPTCHA_ERROR("Sorry, but you are a robot. Goodbye!");

    private String message;

}
