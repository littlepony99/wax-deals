package com.vinylteam.vinyl.service;

public interface CaptchaService {

    byte[] getCaptcha(String captchaId);

    boolean validateCaptcha(String captchaId, String response);

}