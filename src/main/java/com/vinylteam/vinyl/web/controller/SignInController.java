package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/signIn")
public class SignInController {
    private final UserService userService;
    private final Integer sessionMaxInactiveInterval;

    public SignInController(UserService userService,
                            @Value("${session.maxInactiveInterval}") Integer sessionMaxInactiveInterval) {
        this.userService = userService;
        this.sessionMaxInactiveInterval = sessionMaxInactiveInterval;
    }

    @GetMapping
    public String getSignInPage(@SessionAttribute(value = "user", required = false) User user,
                                Model model) {
        WebUtils.setUserAttributes(user, model);
        return "signIn";
    }

    @PostMapping
    public ModelAndView signIn(@SessionAttribute(value = "user", required = false) User user,
                               Model model,
                               HttpSession session,
                               @RequestParam(value = "email") String email,
                               @RequestParam(value = "password") String password) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("email", email);
        WebUtils.setUserAttributes(user, model);
        UserInfoRequest userInfo = UserInfoRequest.builder()
                .email(email)
                .password(password)
                .build();
        userService.signInCheck(userInfo);
        return modelAndView;
    }

}
