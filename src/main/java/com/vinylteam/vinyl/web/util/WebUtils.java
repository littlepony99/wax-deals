package com.vinylteam.vinyl.web.util;

import com.vinylteam.vinyl.entity.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

public class WebUtils {

    public static void setUserAttributes(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                model.addAttribute("userRole", user.getRole().toString());
            }
        }
    }
}
