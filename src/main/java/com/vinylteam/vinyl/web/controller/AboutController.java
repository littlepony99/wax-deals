package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(path = "/about")
public class AboutController {

    @GetMapping
    public String getAboutPage(HttpSession session,
                               Model model) {
        WebUtils.setUserAttributes(session, model);
        return "about";
    }
}
