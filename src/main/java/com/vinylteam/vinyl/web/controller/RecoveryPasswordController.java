package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.exception.RecoveryPasswordException;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/recoveryPassword")
@RequiredArgsConstructor
public class RecoveryPasswordController {
    private final RecoveryPasswordService recoveryPasswordService;

    @GetMapping
    public String getRecoveryPasswordPage(HttpSession session,
                                          HttpServletResponse response,
                                          Model model) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        WebUtils.setUserAttributes(session, model);
        return "recoveryPassword";
    }

    @PostMapping
    public String sendRecoveryToken(HttpSession session,
                                    @RequestParam(value = "email") String email,
                                    HttpServletResponse response,
                                    Model model) {
        response.setContentType("text/html;charset=utf-8");
        model.addAttribute("email", email);
        WebUtils.setUserAttributes(session, model);
        try {
            recoveryPasswordService.sendLink(email);
            log.debug("Successfully send mail for recovery password to email - {'email':{}}", email);
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
            model.addAttribute("message", "Follow the link that we sent you by email - " + email);
        } catch (RecoveryPasswordException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            log.error(e.getMessage());
            model.addAttribute("message", e.getMessage());
        }
        return "recoveryPassword";
    }
}
