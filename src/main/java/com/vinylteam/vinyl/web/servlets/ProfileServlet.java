package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProfileServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//        Map<String, String> attributes = new HashMap<>();
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            User user = (User) session.getAttribute("user");
//            if (user != null) {
//                attributes.put("userRole", user.getRole().toString());
//                attributes.put("email", user.getEmail());
//                attributes.put("discogsUserName", user.getDiscogsUserName());
//            }
//        }
//        PageGenerator.getInstance().process("profile", attributes, response.getWriter());
    }

}
