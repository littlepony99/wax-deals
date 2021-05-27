package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
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

@Slf4j
public class EditProfileServlet extends HttpServlet {

    private SecurityService securityService;
    private UserService userService;

    public EditProfileServlet(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

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
                attributes.put("email", user.getEmail());
                attributes.put("discogsUserName", user.getDiscogsUserName());
            }
        }
        PageGenerator.getInstance().process("editProfile", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> attributes = new HashMap<>();
        response.setContentType("text/html;charset=utf-8");
        String newEmail = request.getParameter("email");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");
        String newDiscogsUserName = request.getParameter("discogsUserName");
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            User user = (User) httpSession.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
                String email = user.getEmail();
                String discogsUserName = user.getDiscogsUserName();
                if (!newPassword.equals(confirmNewPassword)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                    attributes.put("message", "Sorry, passwords don't match!");
                } else {
                    boolean checkOldPassword = securityService.checkPasswordAgainstUserPassword(user, oldPassword.toCharArray());
                    if (checkOldPassword) {
                        boolean isUpdated;
                        if (!newPassword.equals("")) {
                            isUpdated = userService.update(email, newEmail, newPassword, newDiscogsUserName);
                            log.debug("Trying update user with new email and new password in the database {'newEmail':{}}.", newEmail);
                        } else {
                            isUpdated = userService.update(email, newEmail, oldPassword, newDiscogsUserName);
                            log.debug("Trying update user with new email and old password in the database {'newEmail':{}}.", newEmail);
                        }
                        if (isUpdated) {
                            log.info("User was updated with new email or password in the database {'newEmail':{}}.", newEmail);
                            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                            attributes.put("message", "Your profile is successfully changed.");
                            httpSession.invalidate();
                            email = newEmail;
                            discogsUserName = newDiscogsUserName;
                            HttpSession newSession = request.getSession(true);
                            newSession.setMaxInactiveInterval(60 * 60 * 5);
                            newSession.setAttribute("user", userService.getByEmail(email).orElse(user));
                        } else {
                            log.info("Failed to update with new email or password user in the database {'newEmail':{}}.", newEmail);
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                            attributes.put("message", "Edit is fail! Try again!");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                        attributes.put("message", "Sorry, old password isn't correct!");
                    }
                }
                attributes.put("discogsUserName", discogsUserName);
                attributes.put("email", email);
                PageGenerator.getInstance().process("editProfile", attributes, response.getWriter());
            } else {
                response.sendRedirect("/signIn");
            }
        } else {
            response.sendRedirect("/signIn");
        }
    }

}
