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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/recoveryPassword")
@RequiredArgsConstructor
public class RecoveryPasswordController {
    private final RecoveryPasswordService recoveryPasswordService;

    @GetMapping
    public String getRecoveryPasswordPage(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Model model) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        WebUtils.setUserAttributes(request, model);
        return "recoveryPassword";
    }

    @PostMapping
    public String sendRecoveryToken(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Model model) {
        response.setContentType("text/html;charset=utf-8");
        String email = request.getParameter("email");
        model.addAttribute("email", email);
        WebUtils.setUserAttributes(request, model);
        try {
            recoveryPasswordService.sendLink(email);
            log.debug("Successfully send mail for recovery password to email - {'email':{}}", email);
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
            model.addAttribute("message", "Follow the link that we sent you by email - " + email);
//        } catch (RecoveryPasswordException e) {
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            log.error(e.getMessage());
            model.addAttribute("message", e.getMessage());
        }
        return "recoveryPassword";
    }
}
