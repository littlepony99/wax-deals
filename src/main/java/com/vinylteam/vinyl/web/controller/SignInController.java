package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.JwtTokenProvider;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
//@Controller
@CrossOrigin(origins = {"http://localhost:3000", "http://react-wax-deals.herokuapp.com"})
public class SignInController {
    private final UserService userService;
    private final Integer sessionMaxInactiveInterval;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public SignInController(UserService userService,
                            @Value("${session.maxInactiveInterval}") Integer sessionMaxInactiveInterval,
                            AuthenticationManager authenticationManager,
                            JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.sessionMaxInactiveInterval = sessionMaxInactiveInterval;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/signIn")
    public String getSignInPage(@SessionAttribute(value = "user", required = false) User user,
                                Model model) {

        WebUtils.setUserAttributes(user, model);
        return "signIn";
    }

    //@PostMapping("/emailConfirmation")
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
