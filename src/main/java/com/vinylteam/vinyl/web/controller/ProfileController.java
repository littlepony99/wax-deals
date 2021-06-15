package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public String getProfilePage(HttpServletRequest request,
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
        return "profile";
    }
}
