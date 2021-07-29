package com.vinylteam.vinyl.web.util;

import com.vinylteam.vinyl.entity.User;
import org.springframework.ui.Model;

public class WebUtils {

    public static void setUserAttributes(User user, Model model) {
        if (user != null) {
            model.addAttribute("userRole", user.getRole().getName());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("discogsUserName", user.getDiscogsUserName());
        }
    }

}
