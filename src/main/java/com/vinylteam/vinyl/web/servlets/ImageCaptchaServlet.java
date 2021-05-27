package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.service.impl.DefaultCaptchaService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ImageCaptchaServlet extends HttpServlet {
    private DefaultCaptchaService captchaService = new DefaultCaptchaService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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