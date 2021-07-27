package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/signUp")
@CrossOrigin(origins = { "http://localhost:3000", "http://react-wax-deals.herokuapp.com" })
public class SignUpController {
    private final UserService userService;

    @GetMapping
    public String getRegistrationPage(@SessionAttribute(value = "user", required = false) User user,
                                      Model model) {
        WebUtils.setUserAttributes(user, model);
        return "registration";
    }

    @PostMapping
    public ModelAndView signUpUser(@SessionAttribute(value = "user", required = false) User user,
                                   @RequestParam(value = "email") String email,
                                   @RequestParam(value = "password") String password,
                                   @RequestParam(value = "confirmPassword") String confirmPassword,
                                   @RequestParam(value = "discogsUserName") String discogsUserName,
                                   Model model) {
        WebUtils.setUserAttributes(user, model);
        UserInfoRequest userProfileInfo = UserInfoRequest.builder()
                .email(email)
                .password(password)
                .passwordConfirmation(confirmPassword)
                .discogsUserName(discogsUserName)
                .build();
        ModelAndView modelAndView = new ModelAndView("registration");
        modelAndView.addObject("email", email);
        modelAndView.addObject("discogsUserName", discogsUserName);
        userService.register(userProfileInfo);
        log.debug("User was added with " +
                "passed email and password to db {'email':{}}", email);
        modelAndView.setStatus(HttpStatus.SEE_OTHER);
        log.debug("Set response status to {'status':{}}", HttpStatus.SEE_OTHER);
        modelAndView.addObject("message", "Please confirm your registration. To do this, follow the link that we sent to your email - " + email);
        return modelAndView;
    }

    void setBadRequest(ModelAndView modelAndView, String message) {
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        log.debug("Set response status to {'status':{}}", HttpStatus.BAD_REQUEST);
        modelAndView.addObject("message", message);
    }

}
