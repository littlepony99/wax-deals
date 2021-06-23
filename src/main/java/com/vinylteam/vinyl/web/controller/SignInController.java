package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

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
        Optional<User> optionalUser = userService.signInCheck(email, password);
        log.debug("Received a optional with User with password verification by the passed " +
                "email address and password {'email':{}, 'optionalUser':{}}", email, optionalUser);
        if (optionalUser.isPresent()) {
            if (optionalUser.get().getStatus()) {
                log.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                modelAndView.setStatus(HttpStatus.OK);
                log.debug("Set response status to {'status':{}}", HttpStatus.OK);
                User checkedUser = optionalUser.get();
                session.setMaxInactiveInterval(sessionMaxInactiveInterval);
                session.setAttribute("user", checkedUser);
                modelAndView.setViewName("redirect:/");
            } else {
                log.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                modelAndView.setStatus(HttpStatus.SEE_OTHER);
                log.debug("Set response status to {'status':{}}", HttpStatus.SEE_OTHER);
                modelAndView.addObject("message", "Sorry, your email has not been verified. Please go to your mailbox and follow the link to confirm your registration.");
                modelAndView.setViewName("confirmation-directions");
            }
        } else {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpStatus.BAD_REQUEST);
            modelAndView.addObject("message", "Sorry, login or password is not correct, please, check your credentials and try again.");
            modelAndView.setViewName("signIn");
        }
        return modelAndView;
    }
}
