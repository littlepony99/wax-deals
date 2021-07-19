package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.web.dto.AddUserPostDto;
import com.vinylteam.vinyl.web.dto.CaptchaResponseDto;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/contact")
public class ContactUsController {
    private final UserPostService userPostService;
    @Value("${project.mail}")
    private String projectMail;

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
                                        @RequestBody AddUserPostDto dto) throws ForbiddenException {
        response.setContentType("text/html;charset=utf-8");
        try {
            Boolean isSuccess = userPostService.addUserPostWithCaptchaRequest(dto);
            if (isSuccess) {
                return new CaptchaResponseDto("Thank you. Your request was sent to us. We contact with you as soon as possible.");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.info("Post not added");
                return new CaptchaResponseDto("Sorry. but your request wasn't sent to us. Please, write to us - " + projectMail);
            }
        } catch (ForbiddenException e) {
            WebUtils.setUserAttributes(user, model);
            throw new ForbiddenException("INVALID_CAPTCHA");
        }
    }
}
