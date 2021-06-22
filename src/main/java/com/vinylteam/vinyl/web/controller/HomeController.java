package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping
public class HomeController {

    @GetMapping
    public String getHomePage(@SessionAttribute(value = "user", required = false) User user,
                              Model model) {
        WebUtils.setUserAttributes(user, model);
        return "index";
    }
}
