package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ChangePasswordServlet extends HttpServlet {

    private final RecoveryPasswordService recoveryPasswordService;

    public ChangePasswordServlet(RecoveryPasswordService recoveryPasswordService) {
        this.recoveryPasswordService = recoveryPasswordService;
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
        Timestamp tokenLifetime;
        Optional<RecoveryToken> optionalToken = recoveryPasswordService.getByRecoveryToken(token);
        if (optionalToken.isPresent()) {
            token = optionalToken.get().getToken();
            tokenLifetime = optionalToken.get().getLifeTime();
            if (tokenLifetime.compareTo(Timestamp.from(Instant.now())) > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
                attributes.put("recoveryToken", token);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                log.debug("Token lifetime has come to an end.");
                boolean isDeleted = recoveryPasswordService.removeRecoveryUserToken(token);
                if (isDeleted) {
                    log.debug("User recovery token was deleted from recovery_password in db {'token':{}}.", token);
                } else {
                    log.error("Error. User recovery token wasn't deleted from recovery_password in db {'token':{}}.", token);
                }
                attributes.put("errorMessage", "The link you are trying to follow is no longer valid. You need to" +
                        " repeat the recovery password process.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("During recovery password user token is not correct or is not exist.");
            attributes.put("errorMessage", "Your link is incorrect! Please check the link in the your email or contact support.");
        }
        PageGenerator.getInstance().process("newPassword", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        String token = request.getParameter("recoveryToken");
        long userId = 0L;
        if (newPassword == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            attributes.put("message", "Sorry, the password is empty!");
        } else {
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
                    Optional<RecoveryToken> recoveryToken = recoveryPasswordService.getByRecoveryToken(token);
                    Optional<User> optionalUser = Optional.empty();
                    if (recoveryToken.isPresent()) {
                        userId = recoveryToken.get().getUserId();
                        optionalUser = recoveryPasswordService.findById(userId);
                    }
                    if (optionalUser.isPresent()) {
                        token = recoveryToken.get().getToken();
                        User user = optionalUser.get();
                        attributes.put("recoveryToken", token);

                        String email = user.getEmail();
                        boolean isUpdated = recoveryPasswordService.update(email, email, newPassword, user.getDiscogsUserName());
                        log.debug("Got result of changing password of user with " +
                                "passed new password to db {'email':{}, 'isAdded':{}}", email, isUpdated);
                        if (isUpdated) {
                            boolean isDeleted = recoveryPasswordService.removeRecoveryUserToken(token);
                            if (isDeleted) {
                                log.debug("User recovery token was deleted from recovery_password in db {'token':{}}.", token);
                            } else {
                                log.error("Error. User recovery token wasn't deleted from recovery_password in db {'token':{}}.", token);
                            }
                            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                            attributes.put("message", "Your password was changed. Please, try to log in use new password.");
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                            attributes.put("message", "Sorry, but password couldn't be changed. Try again or contact support.");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        log.debug("During recovery password user token is not correct {'userId':{}, 'token':{}}.", userId, token);
                        attributes.put("errorMessage", "Your link is incorrect! Please check the link in the your email or contact support.");
                    }
                }
            }
        }
        PageGenerator.getInstance().process("signIn", attributes, response.getWriter());

    }
}
