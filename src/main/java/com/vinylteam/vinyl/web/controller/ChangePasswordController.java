package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.RecoveryPasswordException;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/newPassword")
public class ChangePasswordController {

    private final RecoveryPasswordService recoveryPasswordService;

    @GetMapping
    public ModelAndView getChangePasswordPage(@SessionAttribute(value = "user", required = false) User user,
                                              @RequestParam(value = "token") String token,
                                              Model model) {
        WebUtils.setUserAttributes(user, model);
        ModelAndView modelAndView = new ModelAndView("newPassword");
        try {
            recoveryPasswordService.checkToken(token);
            modelAndView.addObject("recoveryToken", token);
        } catch (RecoveryPasswordException e) {
            log.debug("Set response status to {'status':{}}, error - {}", HttpStatus.BAD_REQUEST, e.getMessage());
            modelAndView.addObject("errorMessage", e.getMessage());
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @PostMapping
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
            recoveryPasswordService.changePassword(userProfileInfo);
            modelAndView.setStatus(HttpStatus.SEE_OTHER);
            modelAndView.addObject("message", "Your password was changed. Please, try to log in use new password.");
        } catch (RecoveryPasswordException e) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            log.debug("Set response status to {'status':{}}, error - {}", HttpStatus.BAD_REQUEST, e.getMessage());
            modelAndView.addObject("message", e.getMessage());
        }
        return modelAndView;
    }
}
