package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/emailConfirmation")
public class ConfirmationController {

    private final UserService userService;
    private final ConfirmationService confirmationService;
    private final Integer sessionMaxInactiveInterval;

    public ConfirmationController(UserService userService,
                                  ConfirmationService confirmationService,
                                  @Value("${session.maxInactiveInterval}") Integer sessionMaxInactiveInterval) {
        this.userService = userService;
        this.confirmationService = confirmationService;
        this.sessionMaxInactiveInterval = sessionMaxInactiveInterval;
    }

    @GetMapping
    public String getConfirmationPage(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Model model) {
        String tokenAsString = request.getParameter("token");
        Optional<ConfirmationToken> optionalConfirmationToken = confirmationService.findByToken(tokenAsString);
        response.setContentType("text/html;charset=utf-8");
        if (optionalConfirmationToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_NOT_FOUND);
            model.addAttribute("message", "Sorry, such confirmation link does not exist. Maybe your email is already verified? Try to log in! " +
                    "If you haven't verified your email and you can't use confirmation link, please, contact us through the form.");
            return "confirmation-directions";
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
            model.addAttribute("token", tokenAsString);
            return "confirmation-signin";
        }
    }

    @PostMapping
    public String confirmSignIn(HttpServletRequest request,
                                HttpServletResponse response,
                                Model model) {
        String tokenAsString = request.getParameter("token");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        log.info("Sign in user with email {} and token {}", email, tokenAsString);
        Optional<User> optionalUser = userService.signInCheck(email, password, tokenAsString);
        response.setContentType("text/html;charset=utf-8");
        log.debug("Received an optional with User with password verification by the passed " +
                "email address and password {'email':{}, 'optionalUser':{}}", email, optionalUser);
        if (optionalUser.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("token", tokenAsString);
            model.addAttribute("message", "Sorry, login or password is not correct, please check yours credentials and try again.");
            return "confirmation-signin";
        } else {
            User user = optionalUser.get();
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(sessionMaxInactiveInterval);
            session.setAttribute("user", user);
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
            return "index";
        }
    }
}
