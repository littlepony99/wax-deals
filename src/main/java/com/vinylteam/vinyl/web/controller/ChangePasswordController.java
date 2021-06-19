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
@RequiredArgsConstructor
@Controller
@RequestMapping("/newPassword")
public class ChangePasswordController {

    private final RecoveryPasswordService recoveryPasswordService;

    @GetMapping
    public String getChangePasswordPage(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Model model) {
        response.setContentType("text/html;charset=utf-8");
        WebUtils.setUserAttributes(request, model);
        String token = request.getParameter("token");
        try {
            recoveryPasswordService.checkToken(token);
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
            model.addAttribute("recoveryToken", token);
        } catch (RecoveryPasswordException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}, error - {}", HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "newPassword";
    }

    @PostMapping
    public String changePassword(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Model model) {
        response.setContentType("text/html;charset=utf-8");
        WebUtils.setUserAttributes(request, model);
        String newPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String token = request.getParameter("recoveryToken");
        try {
            recoveryPasswordService.changePassword(newPassword, confirmPassword, token);
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
            model.addAttribute("message", "Your password was changed. Please, try to log in use new password.");
        } catch (RecoveryPasswordException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}, error - {}", HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            model.addAttribute("message", e.getMessage());
        }
        return "signIn";
    }
}
