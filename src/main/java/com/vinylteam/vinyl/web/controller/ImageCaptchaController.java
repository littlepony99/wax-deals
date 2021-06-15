package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.service.impl.DefaultCaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class ImageCaptchaController {
    private final DefaultCaptchaService captchaService;

    @GetMapping
    public void getCaptcha(HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        String captchaId = request.getSession().getId();
        byte[] captchaChallengeAsJpeg = captchaService.getCaptcha(captchaId);

        response.setContentType("image/jpeg");
        response.setStatus(HttpServletResponse.SC_OK);
        ServletOutputStream responseOutputStream =
                response.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }
}
