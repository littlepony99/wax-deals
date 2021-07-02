package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.PasswordRecoveryException;
import com.vinylteam.vinyl.service.PasswordRecoveryService;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("/recoveryPassword")
@RequiredArgsConstructor
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    @GetMapping
    public String getPasswordRecoveryPage(@SessionAttribute(value = "user", required = false) User user,
                                          Model model) {
        WebUtils.setUserAttributes(user, model);
        return "recoveryPassword";
    }

    @PostMapping
    public ModelAndView sendRecoveryToken(@SessionAttribute(value = "user", required = false) User user,
                                          @RequestParam(value = "email") String email,
                                          Model model) {
        ModelAndView modelAndView = new ModelAndView("recoveryPassword");
        model.addAttribute("email", email);
        WebUtils.setUserAttributes(user, model);
        try {
            passwordRecoveryService.sendLink(email);
            log.debug("Successfully send mail for recovery password to email - {'email':{}}", email);
            modelAndView.setStatus(HttpStatus.OK);
            log.debug("Set response status to {'status':{}}", HttpStatus.OK);
            model.addAttribute("message", "Follow the link that we sent you by email - " + email);
        } catch (PasswordRecoveryException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpStatus.BAD_REQUEST);
            log.error(e.getMessage());
            model.addAttribute("message", e.getMessage());
        }
        return modelAndView;
    }

    @GetMapping("/newPassword")
    public ModelAndView getChangePasswordPage(@SessionAttribute(value = "user", required = false) User user,
                                              @RequestParam(value = "token") String token,
                                              Model model) {
        WebUtils.setUserAttributes(user, model);
        ModelAndView modelAndView = new ModelAndView("newPassword");
        try {
            passwordRecoveryService.checkToken(token);
            modelAndView.addObject("recoveryToken", token);
        } catch (PasswordRecoveryException e) {
            log.debug("Set response status to {'status':{}}, error - {}", HttpStatus.BAD_REQUEST, e.getMessage());
            modelAndView.addObject("errorMessage", e.getMessage());
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @PostMapping("/newPassword")
    public ModelAndView changePassword(@SessionAttribute(value = "user", required = false) User user,
                                       @RequestParam(value = "password") String newPassword,
                                       @RequestParam(value = "confirmPassword") String confirmPassword,
                                       @RequestParam(value = "recoveryToken") String token,
                                       Model model) {
        WebUtils.setUserAttributes(user, model);
        UserChangeProfileInfoRequest userProfileInfo = UserChangeProfileInfoRequest.builder()
                .newPassword(newPassword)
                .confirmNewPassword(confirmPassword)
                .token(token)
                .build();
        ModelAndView modelAndView = new ModelAndView("signIn");
        try {
            passwordRecoveryService.changePassword(userProfileInfo);
            modelAndView.setStatus(HttpStatus.SEE_OTHER);
            modelAndView.addObject("message", "Your password was changed. Please, try to log in use new password.");
        } catch (PasswordRecoveryException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            log.debug("Set response status to {'status':{}}, error - {}", HttpStatus.BAD_REQUEST, e.getMessage());
            modelAndView.addObject("message", e.getMessage());
        }
        return modelAndView;
    }

}
