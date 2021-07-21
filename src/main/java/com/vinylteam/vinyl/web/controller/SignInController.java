package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@Controller
public class SignInController {
    private final UserService userService;
    private final Integer sessionMaxInactiveInterval;

    public SignInController(UserService userService,
                            @Value("${session.maxInactiveInterval}") Integer sessionMaxInactiveInterval) {
        this.userService = userService;
        this.sessionMaxInactiveInterval = sessionMaxInactiveInterval;
    }

    @GetMapping("/signIn")
    public String getSignInPage(@SessionAttribute(value = "user", required = false) User user,
                                Model model) {

        WebUtils.setUserAttributes(user, model);
        return "signIn";
    }

    @PostMapping("/signIn")
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

    @PostMapping("/emailConfirmation")
    public ModelAndView confirmSignIn(@RequestParam(value = "token") String tokenAsString,
                                      @RequestParam(value = "email") String email,
                                      @RequestParam(value = "password") String password,
                                      //@Value("${session.maxInactiveInterval}") Integer sessionMaxInactiveInterval,
                                      HttpSession session) {

        UserInfoRequest userProfileInfo = UserInfoRequest.builder()
                .token(tokenAsString)
                .email(email)
                .password(password)
                .build();

        ModelAndView modelAndView = new ModelAndView();
        log.info("Sign in user with email {} and token {}", email, tokenAsString);
        User user = userService.confirmEmail(userProfileInfo);
        session.setMaxInactiveInterval(sessionMaxInactiveInterval);
        session.setAttribute("user", user);
        modelAndView.setStatus(HttpStatus.OK);
        log.debug("Set response status to {'status':{}}", HttpStatus.OK);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

}
