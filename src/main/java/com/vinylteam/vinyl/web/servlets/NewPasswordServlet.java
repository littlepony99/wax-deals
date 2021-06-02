package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserPostService;
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
public class NewPasswordServlet extends HttpServlet {

    private final UserService userService;
    private final UserPostService userPostService;

    public NewPasswordServlet(UserService userService, UserPostService userPostService) {
        this.userService = userService;
        this.userPostService = userPostService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        Map<String, String> attributes = new HashMap<>();
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
            }
        }
        String token = request.getParameter("token");
        long userId = userPostService.getRecoveryUserId(token);
        if (userId > 0) {
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
            attributes.put("recoveryToken", token);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("During recovery password user token is not correct {'userId':{}, 'token':{}}.", userId, token);
            attributes.put("errorMessage", "Your link is incorrect! Please check the link in the your email or contact support.");
        }
        PageGenerator.getInstance().process("newPassword", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/html;charset=utf-8");
        Map<String, String> attributes = new HashMap<>();
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
            }
        }
        String newPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String recoveryToken = request.getParameter("recoveryToken");
        long userId = userPostService.getRecoveryUserId(recoveryToken);
        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (newPassword.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                attributes.put("message", "Sorry, the password is empty!");
            } else {
                if (!newPassword.equals(confirmPassword)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                    attributes.put("message", "Sorry, the passwords don't match!");
                } else {
                    String email = user.getEmail();
                    boolean isUpdated = userService.update(email, email, newPassword, user.getDiscogsUserName());
                    log.debug("Got result of changing password of user with " +
                            "passed new password to db {'email':{}, 'isAdded':{}}", email, isUpdated);
                    if (isUpdated) {
                        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                        attributes.put("message", "Your password was changed. Please, try to log in use new password.");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                        attributes.put("message", "Sorry, but password couldn't be changed. Try again or contact support.");
                    }
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("During recovery password user token is not correct {'userId':{}, 'token':{}}.", userId, recoveryToken);
            attributes.put("errorMessage", "Your link is incorrect! Please check the link in the your email or contact support.");
        }

        PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
    }
}
