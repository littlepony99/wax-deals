package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/emailConfirmation")
public class ConfirmationController {

    private final UserService userService;
    private final ConfirmationService confirmationService;

    @GetMapping
    public ModelAndView getConfirmationPage(@RequestParam(value = "token") String tokenAsString) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<ConfirmationToken> optionalConfirmationToken = confirmationService.findByToken(tokenAsString);
        if (optionalConfirmationToken.isEmpty()) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            log.debug("Set response status to {'status':{}}", HttpStatus.NOT_FOUND);
            modelAndView.addObject("message", "Sorry, such confirmation link does not exist. Maybe your email is already verified? Try to log in! " +
                    "If you haven't verified your email and you can't use confirmation link, please, contact us through the form.");
            modelAndView.setViewName("confirmation-directions");
        } else {
            modelAndView.setStatus(HttpStatus.OK);
            log.debug("Set response status to {'status':{}}", HttpStatus.OK);
            modelAndView.addObject("token", tokenAsString);
            modelAndView.setViewName("confirmation-signin");
        }
        return modelAndView;
    }

    //TODO: Move to future user controller.
    @PostMapping
    public ModelAndView confirmSignIn(@RequestParam(value = "token") String tokenAsString,
                                      @RequestParam(value = "email") String email,
                                      @RequestParam(value = "password") String password,
                                      @Value("${session.maxInactiveInterval}") Integer sessionMaxInactiveInterval,
                                      HttpSession session) {
        UserInfoRequest userProfileInfo = UserInfoRequest.builder()
                .token(tokenAsString)
                .email(email)
                .password(password)
                .build();
        ModelAndView modelAndView = new ModelAndView();
        log.info("Sign in user with email {} and token {}", email, tokenAsString);
        userService.signInCheck(userProfileInfo);
        //FIXME: temporary variable, refactor code to not use it.
        Optional<User> optionalUser = Optional.of(new User());
        if (optionalUser.isEmpty()) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpStatus.BAD_REQUEST);
            modelAndView.addObject("token", tokenAsString);
            modelAndView.addObject("message", "Sorry, login or password is not correct, please check yours credentials and try again.");
            modelAndView.setViewName("confirmation-signin");
        } else {
            User user = optionalUser.get();
            session.setMaxInactiveInterval(sessionMaxInactiveInterval);
            session.setAttribute("user", user);
            modelAndView.setStatus(HttpStatus.OK);
            log.debug("Set response status to {'status':{}}", HttpStatus.OK);
            modelAndView.setViewName("redirect:/");
        }
        return modelAndView;
    }

}
