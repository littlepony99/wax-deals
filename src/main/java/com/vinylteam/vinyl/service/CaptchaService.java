package com.vinylteam.vinyl.service;

public interface CaptchaService {

    boolean validateCaptcha(final String captchaResponse);

}