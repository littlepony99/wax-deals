package com.vinylteam.vinyl.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/signOut")
public class SignOutController {

    @GetMapping
    public String getSignOutPage(HttpSession session) {
        log.info("Session was invalidate for {user:{}}", session.getAttribute("user"));
        session.invalidate();
        return "redirect:/";
    }
}
