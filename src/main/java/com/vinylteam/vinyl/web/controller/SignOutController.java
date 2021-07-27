package com.vinylteam.vinyl.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/signOut")
@CrossOrigin(origins = { "http://localhost:3000", "http://react-wax-deals.herokuapp.com" })
public class SignOutController {

    @GetMapping
    public String getSignOutPage(HttpSession session) {
        log.info("Session was invalidate for {user:{}}", session.getAttribute("user"));
        session.invalidate();
        return "redirect:/";
    }

}
