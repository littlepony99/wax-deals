package com.vinylteam.vinyl.web.util;

import com.vinylteam.vinyl.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

public class WebUtils {

    public static void setUserAttributes(HttpServletRequest request, Map<String, String> attributes) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
            }
        }
    }
}
