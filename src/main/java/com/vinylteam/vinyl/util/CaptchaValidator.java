package com.vinylteam.vinyl.util;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

public class CaptchaValidator {

    private static ImageCaptchaService imageCaptchaService = new DefaultManageableImageCaptchaService();

    public static ImageCaptchaService getImageCaptchaService() {
        return imageCaptchaService;
    }

}