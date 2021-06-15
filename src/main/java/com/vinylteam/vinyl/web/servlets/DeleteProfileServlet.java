package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DeleteProfileServlet extends HttpServlet {

    private final UserService userService;

    public DeleteProfileServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        Map<String, String> attributes = new HashMap<>();
//        HttpSession httpSession = request.getSession(false);
//        if (httpSession != null) {
//            User user = (User) httpSession.getAttribute("user");
//            if (user != null) {
//                boolean isDeleted = userService.delete(user);
//                if (isDeleted) {
//                    response.setStatus(HttpServletResponse.SC_OK);
//                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//                    httpSession.invalidate();
//                    response.sendRedirect("/signUp");
//                } else {
//                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
//                    attributes.put("message", "Delete is fail! Try again!");
//                    PageGenerator.getInstance().process("editProfile", attributes, response.getWriter());
//                }
//            } else {
//                response.sendRedirect("/signIn");
//            }
//        } else {
//            response.sendRedirect("/signIn");
//        }
    }

}
