package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping(path = "/about")
@CrossOrigin(origins = { "http://localhost:3000", "http://react-wax-deals.herokuapp.com" })
public class AboutController {

    @GetMapping
    public String getAboutPage(@SessionAttribute(value = "user", required = false) User user,
                               Model model) {
        WebUtils.setUserAttributes(user, model);
        return "about";
    }

}
