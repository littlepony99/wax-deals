package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ConfirmationService;
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
import java.util.UUID;

@Slf4j
public class ConfirmationServlet extends HttpServlet {

    private final UserService userService;
    private final ConfirmationService confirmationService;

    public ConfirmationServlet(UserService userService, ConfirmationService confirmationService) {
        this.userService = userService;
        this.confirmationService = confirmationService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tokenAsString = request.getParameter("token");
        UUID token = null;
        try {
            token = UUID.fromString(tokenAsString);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid email confirmation link token parameter {'token':{}}", tokenAsString, e);
        }
        Optional<ConfirmationToken> optionalConfirmationToken = confirmationService.findByToken(token);
        response.setContentType("text/html;charset=utf-8");
        Map<String, String> attributes = new HashMap<>();
        if (optionalConfirmationToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_NOT_FOUND);
            attributes.put("message", "Sorry, such confirmation link does not exist. Maybe your email is already verified? Try to log in! " +
                    "If you haven't verified your email and you can't use confirmation link, please, contact us through the form.");
            PageGenerator.getInstance().process("confirmation-directions", attributes, response.getWriter());
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
            attributes.put("token", tokenAsString);
            PageGenerator.getInstance().process("confirmation-signin", attributes, response.getWriter());
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tokenAsString = request.getParameter("token");
        UUID token = null;
        try {
            token = UUID.fromString(tokenAsString);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid email confirmation link token parameter {'token':{}}", tokenAsString, e);
        }
        Optional<ConfirmationToken> optionalConfirmationTokenByLinkToken = confirmationService.findByToken(token);
        response.setContentType("text/html;charset=utf-8");
        Map<String, String> attributes = new HashMap<>();
        if (optionalConfirmationTokenByLinkToken.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            attributes.put("message", "Sorry, something went wrong on out side, we're looking into it. Please, try to login again, or contact us.");
            PageGenerator.getInstance().process("confirmation-directions", attributes, response.getWriter());
        } else {
            ConfirmationToken confirmationTokenByLinkToken = optionalConfirmationTokenByLinkToken.get();
            User userByLinkToken = userService.findById(confirmationTokenByLinkToken.getUserId()).get();
            String email = request.getParameter("email");
            if (userByLinkToken.getEmail().equalsIgnoreCase(email)) {
                String password = request.getParameter("password");
                Optional<User> optionalUser = userService.signInCheck(email, password);
                log.debug("Received a optional with User with password verification by the passed " +
                        "email address and password {'email':{}, 'optionalUser':{}}", email, optionalUser);
                if (optionalUser.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                    attributes.put("message", "Sorry, login or password is not correct, please check yours credentials and try again.");
                    PageGenerator.getInstance().process("confirmation-signin", attributes, response.getWriter());
                } else {
                    User user = optionalUser.get();
                    userService.update(user.getEmail(), user.getEmail(), password, user.getDiscogsUserName());
                    confirmationService.deleteByUserId(user.getId());
                    HttpSession session = request.getSession(true);
                    session.setMaxInactiveInterval(60 * 60 * 5);
                    session.setAttribute("user", user);
                    response.setStatus(HttpServletResponse.SC_OK);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
                    response.sendRedirect("/");
                }
            } else {
                log.warn("User tried to confirm wrong email with link {'wrongEmail':{}, 'userByToken':{}}", email, userByLinkToken);
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                attributes.put("message", "Sorry, this isn't your confirmation link. Try to log in again.");
                PageGenerator.getInstance().process("confirmation-directions", attributes, response.getWriter());
            }
        }
    }

}
