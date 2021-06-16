package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
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

@Slf4j
@Controller
@RequestMapping("/editProfile")
public class EditProfileController {
    private final SecurityService securityService;
    private final UserService userService;
    private final Integer sessionMaxInactiveInterval;

    public EditProfileController(SecurityService securityService,
                                 UserService userService,
                                 @Value("${session.maxInactiveInterval}") Integer sessionMaxInactiveInterval) {
        this.securityService = securityService;
        this.userService = userService;
        this.sessionMaxInactiveInterval = sessionMaxInactiveInterval;
    }

    @GetMapping
    public String getEditProfilePage(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Model model) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                model.addAttribute("userRole", user.getRole().toString());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("discogsUserName", user.getDiscogsUserName());
            }
        }
        return "editProfile";
    }

    @PostMapping
    public String editProfile(HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {
        response.setContentType("text/html;charset=utf-8");
        String newEmail = request.getParameter("email");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");
        String newDiscogsUserName = request.getParameter("discogsUserName");
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            User user = (User) httpSession.getAttribute("user");
            if (user != null) {
                model.addAttribute("userRole", user.getRole().toString());
                String email = user.getEmail();
                String discogsUserName = user.getDiscogsUserName();
                if (!newPassword.equals(confirmNewPassword)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                    model.addAttribute("message", "Sorry, passwords don't match!");
                } else {
                    boolean checkOldPassword = securityService.checkPasswordAgainstUserPassword(user, oldPassword.toCharArray());
                    if (checkOldPassword) {
                        boolean isUpdated;
                        if (!newPassword.equals("")) {
                            isUpdated = userService.update(email, newEmail, newPassword, newDiscogsUserName);
                            log.debug("Trying update user with new email and new password in the database {'newEmail':{}}.", newEmail);
                        } else {
                            isUpdated = userService.update(email, newEmail, oldPassword, newDiscogsUserName);
                            log.debug("Trying update user with new email and old password in the database {'newEmail':{}}.", newEmail);
                        }
                        if (isUpdated) {
                            log.info("User was updated with new email or password in the database {'newEmail':{}}.", newEmail);
                            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                            model.addAttribute("message", "Your profile is successfully changed.");
                            httpSession.invalidate();
                            email = newEmail;
                            discogsUserName = newDiscogsUserName;
                            HttpSession newSession = request.getSession(true);
                            newSession.setMaxInactiveInterval(sessionMaxInactiveInterval);
                            newSession.setAttribute("user", userService.findByEmail(email).orElse(user));
                        } else {
                            log.info("Failed to update with new email or password user in the database {'newEmail':{}}.", newEmail);
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                            model.addAttribute("message", "Edit is fail! Try again!");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                        model.addAttribute("message", "Sorry, old password isn't correct!");
                    }
                }
                model.addAttribute("discogsUserName", discogsUserName);
                model.addAttribute("email", email);
                return "editProfile";
            } else {
                return "redirect:/signIn";
            }
        } else {
            return "redirect:/signIn";
        }
    }
}
