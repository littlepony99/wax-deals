package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(path = "/about")
public class AboutController {

    @GetMapping
    public String getAboutPage(@SessionAttribute(value = "user", required = false) User user,
                               Model model) {
        WebUtils.setUserAttributes(user, model);
        return "about";
    }
}
