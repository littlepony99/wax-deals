package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.service.CaptchaService;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.service.impl.DefaultCaptchaService;
import com.vinylteam.vinyl.web.dto.CaptchaRequestDto;
import com.vinylteam.vinyl.web.dto.CaptchaResponseDto;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/contact")
public class ContactUsController {
    private final UserPostService userPostService;
    private final CaptchaService defaultCaptchaService;
    private final DefaultCaptchaService service;

    @GetMapping
    public ModelAndView getContactUsPage(@SessionAttribute(value = "user", required = false) User user,
                                         Model model) {
        WebUtils.setUserAttributes(user, model);
        return new ModelAndView("contactUs");
    }

    @PostMapping
    public CaptchaResponseDto contactUs(@SessionAttribute(value = "user", required = false) User user,
                                        HttpServletResponse response,
                                        Model model,
                                        @Value("${project.mail}") String projectMail,
                                        @RequestBody CaptchaRequestDto dto) throws ForbiddenException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);

        boolean isCaptchaValid = service.validateCaptcha(dto.getCaptchaResponse());

        if (isCaptchaValid) {
            UserPost post = UserPost.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .theme(dto.getSubject())
                    .message(dto.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build();
            try {
                userPostService.processAdd(post);
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Post added");
                return new CaptchaResponseDto("Thank you. Your request was sent to us. We contact with you as soon as possible.");
            } catch (RuntimeException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.info("Post not added");
                return new CaptchaResponseDto("Sorry. but your request wasn't sent to us. Please, write to us - " + projectMail);
            }
        } else {
            WebUtils.setUserAttributes(user, model);
            throw new ForbiddenException("INVALID_CAPTCHA");
        }
    }
}
