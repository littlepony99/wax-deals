package com.vinylteam.vinyl.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserPostErrors {

    INCORRECT_CAPTCHA_ERROR("Sorry, but you did not pass the captcha validation. Please contact us via social media.");

    private String message;

}
