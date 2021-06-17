package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.web.dto.CaptchaRequestDto;
import com.vinylteam.vinyl.web.dto.CaptchaResponseDto;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.service.CaptchaService;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.service.ValidateCaptcha;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/contact")
public class ContactUsController {
    private final UserPostService userPostService;
    private final CaptchaService defaultCaptchaService;
    private final ValidateCaptcha service;

    @GetMapping
    public ModelAndView getContactUsPage(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Model model) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        WebUtils.setUserAttributes(request, model);
        return new ModelAndView("contactUs");
    }

    @PostMapping
    public CaptchaResponseDto sendPost(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Model model,
                                       @RequestBody final CaptchaRequestDto dto) throws ForbiddenException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);

        final boolean isCaptchaValid = service.validateCaptcha(dto.getCaptchaResponse());

        if (isCaptchaValid) {
            UserPost post = new UserPost(dto.getName(), dto.getEmail(), dto.getSubject(), dto.getMessage(), LocalDateTime.now());
            boolean isPostProcessed = userPostService.processAdd(post);
            if (isPostProcessed) {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Post added");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.info("Post not added");
            }
            return new CaptchaResponseDto("Thank you. Your request was sent to us. We contact with you as soon as possible.");
        } else {
            WebUtils.setUserAttributes(request, model);
            throw new ForbiddenException("INVALID_CAPTCHA");
        }
    }
}
