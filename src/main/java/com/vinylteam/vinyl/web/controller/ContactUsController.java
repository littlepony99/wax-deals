package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.service.CaptchaService;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/contact")
public class ContactUsController {
    private final UserPostService userPostService;
    private final CaptchaService defaultCaptchaService;

    @GetMapping
    public String getContactUsPage(HttpServletRequest request,
                                   HttpServletResponse response,
                                   Model model) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        WebUtils.setUserAttributes(request, model);
        return "contactUs";
    }

    @PostMapping
    public String sendPost(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String messageContactUs = request.getParameter("messageContactUs");
        String captcha = request.getParameter("captcha");

        boolean isCaptchaValid = defaultCaptchaService.validateCaptcha(request.getSession().getId(), captcha);

        if (isCaptchaValid) {
            UserPost post = new UserPost(name, email, subject, messageContactUs, LocalDateTime.now());
            boolean isPostProcessed = userPostService.processAdd(post);
            if (isPostProcessed) {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Post added");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.info("Post not added");
            }
            return "index";
        } else {
            WebUtils.setUserAttributes(request, model);
            model.addAttribute("captchaError", "Captcha is invalid!!!");
            return "contactUs";
        }
    }
}
