package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/deleteProfile")
public class DeleteProfileController {
    private final UserService userService;

    @PostMapping
    public String deleteProfile(HttpServletRequest request,
                                HttpServletResponse response,
                                Model model) {
        response.setContentType("text/html;charset=utf-8");
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            User user = (User) httpSession.getAttribute("user");
            if (user != null) {
                boolean isDeleted = userService.delete(user);
                if (isDeleted) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
                    httpSession.invalidate();
                    return "redirect:/signUp";
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                    model.addAttribute("message", "Delete is fail! Try again!");
                    return "editProfile";
                }
            } else {
                return "redirect:/signIn";
            }
        } else {
            return "redirect:/signIn";
        }

    }
}
