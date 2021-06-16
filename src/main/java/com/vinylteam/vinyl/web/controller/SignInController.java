package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.util.WebUtils;
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
    public String getSignInPage(HttpServletRequest request,
                                HttpServletResponse response,
                                Model model) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        WebUtils.setUserAttributes(request, model);
        return "signIn";
    }

    @PostMapping
    public String signIn(HttpServletRequest request,
                         HttpServletResponse response,
                         Model model) {
        response.setContentType("text/html;charset=utf-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        model.addAttribute("email", email);
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                model.addAttribute("userRole", user.getRole().toString());
            }
        }
        Optional<User> optionalUser = userService.signInCheck(email, password);
        log.debug("Received a optional with User with password verification by the passed " +
                "email address and password {'email':{}, 'optionalUser':{}}", email, optionalUser);
        if (optionalUser.isPresent()) {
            if (optionalUser.get().getStatus()) {
                log.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                response.setStatus(HttpServletResponse.SC_OK);
                log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
                User user = optionalUser.get();

                session = request.getSession(true);
                session.setMaxInactiveInterval(sessionMaxInactiveInterval);
                session.setAttribute("user", user);
                return "index";
            } else {
                log.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                model.addAttribute("message", "Sorry, your email has not been verified. Please go to your mailbox and follow the link to confirm your registration.");
                return "confirmation-directions";
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("message", "Sorry, login or password is not correct, please, check your credentials and try again.");
            return "signIn";
        }
    }
}
