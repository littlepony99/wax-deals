package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/signUp")
public class SignUpController {
    private final UserService userService;

    @GetMapping
    public String getRegistrationPage(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Model model) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        WebUtils.setUserAttributes(request, model);
        return "registration";
    }

    @PostMapping
    public String signUpUser(HttpServletRequest request,
                             HttpServletResponse response,
                             Model model) {
        response.setContentType("text/html;charset=utf-8");
        WebUtils.setUserAttributes(request, model);
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String discogsUserName = request.getParameter("discogsUserName");
        model.addAttribute("email", email);
        model.addAttribute("discogsUserName", discogsUserName);
        if (password.equals("")) {
            setBadRequest(response, model, "Sorry, the password is empty!");
            return "registration";
        } else {
            if (!password.equals(confirmPassword)) {
                setBadRequest(response, model, "Sorry, the passwords don't match!");
                return "registration";
            } else {
                boolean isAdded = userService.add(email, password, discogsUserName);
                log.debug("Got result of adding user with " +
                        "passed email and password to db {'email':{}, 'isAdded':{}}", email, isAdded);
                if (isAdded) {
                    response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                    model.addAttribute("message", "Please confirm your registration. To do this, follow the link that we sent to your email - " + email);
                    return "confirmation-directions";
                } else {
                    setBadRequest(response, model, "Sorry, but the user couldn't be registered. Check email, password or discogs username!");
                    return "registration";
                }
            }
        }

    }

    void setBadRequest(HttpServletResponse response, Model model, String message) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
        model.addAttribute("message", message);
    }
}
