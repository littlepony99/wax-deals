package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.exception.RecoveryPasswordException;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ModelAndView getChangePasswordPage(HttpSession session,
                                              @RequestParam(value = "token") String token,
                                              Model model) {
        WebUtils.setUserAttributes(session, model);
        ModelAndView modelAndView = new ModelAndView("newPassword");
        try {
            recoveryPasswordService.checkToken(token);
            modelAndView.addObject("recoveryToken", token);
        } catch (RecoveryPasswordException e) {
            log.debug("Set response status to {'status':{}}, error - {}", HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            modelAndView.addObject("errorMessage", e.getMessage());
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        }
        return modelAndView;
    }

    @PostMapping
    public ModelAndView changePassword(HttpSession session,
                                       @RequestParam(value = "password") String newPassword,
                                       @RequestParam(value = "confirmPassword") String confirmPassword,
                                       @RequestParam(value = "recoveryToken") String token,
                                       Model model) {
        WebUtils.setUserAttributes(session, model);
        ModelAndView modelAndView = new ModelAndView("signIn");
        try {
            recoveryPasswordService.changePassword(newPassword, confirmPassword, token);
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
