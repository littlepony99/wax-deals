package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.service.CaptchaService;
import com.vinylteam.vinyl.util.CaptchaValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class DefaultCaptchaService implements CaptchaService {

    @Override
    public byte[] getCaptcha(String captchaId) {
        try (ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream()) {
            BufferedImage challenge =
                    CaptchaValidator.getImageCaptchaService().getImageChallengeForID(captchaId);

            ImageIO.write(challenge, "jpeg", jpegOutputStream);
            return jpegOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error during captcha generation!", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean validateCaptcha(String captchaId, String response) {
        return CaptchaValidator.getImageCaptchaService().validateResponseForID(captchaId,
                response);
    }

}