package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SignUpServlet extends HttpServlet {

    private final UserService userService;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        Map<String, String> attributes = new HashMap<>();
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
            }
        }
        PageGenerator.getInstance().process("registration", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> attributes = new HashMap<>();
        response.setContentType("text/html;charset=utf-8");
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
            }
        }
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String discogsUserName = request.getParameter("discogsUserName");
        attributes.put("email", email);
        attributes.put("discogsUserName", discogsUserName);
        if (password.equals("")) {
            setBadRequest(response, attributes, "Sorry, the password is empty!");
        } else {
            if (!password.equals(confirmPassword)) {
                setBadRequest(response, attributes, "Sorry, the passwords don't match!");
            } else {
                boolean isAdded = userService.add(email, password, discogsUserName);
                log.debug("Got result of adding user with " +
                        "passed email and password to db {'email':{}, 'isAdded':{}}", email, isAdded);
                if (isAdded) {
                    response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                    attributes.put("message", "Please confirm your registration. To do this, follow the link that we sent to your email");
                    attributes.remove("email");
                    attributes.remove("discogsUserName");
                    PageGenerator.getInstance().process("confirmation-directions", attributes, response.getWriter());
                } else {
                    setBadRequest(response, attributes, "Sorry, but the user couldn't be registered. Check email, password or discogs username!");
                }
            }
        }

    }

    private void setBadRequest(HttpServletResponse response, Map<String, String> attributes, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
        attributes.put("message", message);
        PageGenerator.getInstance().process("registration", attributes, response.getWriter());
    }

}
