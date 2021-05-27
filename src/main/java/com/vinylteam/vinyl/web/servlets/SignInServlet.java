package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SignInServlet extends HttpServlet {

    private final UserService userService;

    public SignInServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, String> attributes = new HashMap<>();
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
            }
        }
        PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("email", email);

        Optional<User> optionalUser = userService.signInCheck(email, password);
        log.debug("Received a optional with User with password verification by the passed " +
                "email address and password {'email':{}, 'optionalUser':{}}", email, optionalUser);
        if (optionalUser.isPresent()) {
            if (optionalUser.get().getStatus()) {
                log.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                response.setStatus(HttpServletResponse.SC_OK);
                log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
                User user = optionalUser.get();
                HttpSession session = request.getSession(true);
                session.setMaxInactiveInterval(60 * 60 * 5);
                session.setAttribute("user", user);
                response.sendRedirect("/");
            } else {
                log.debug("User's status is {'status':{}}", optionalUser.get().getStatus());
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                attributes.put("message", "Sorry, your email has not been verified. Please go to your mailbox and follow the link to confirm your registration.");
                PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            attributes.put("message", "Sorry, login or password is not correct, please check yours credentials and try again.");
            PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
        }
    }

}
