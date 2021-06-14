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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class ConfirmationServlet extends HttpServlet {

    private final UserService userService;
    private final ConfirmationService confirmationService;
    private final Integer sessionMaxInactiveInterval;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String tokenAsString = request.getParameter("token");
//        Optional<ConfirmationToken> optionalConfirmationToken = confirmationService.findByToken(tokenAsString);
//        response.setContentType("text/html;charset=utf-8");
//        Map<String, String> attributes = new HashMap<>();
//        if (optionalConfirmationToken.isEmpty()) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_NOT_FOUND);
//            attributes.put("message", "Sorry, such confirmation link does not exist. Maybe your email is already verified? Try to log in! " +
//                    "If you haven't verified your email and you can't use confirmation link, please, contact us through the form.");
//            PageGenerator.getInstance().process("confirmation-directions", attributes, response.getWriter());
//        } else {
//            response.setStatus(HttpServletResponse.SC_OK);
//            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//            attributes.put("token", tokenAsString);
//            PageGenerator.getInstance().process("confirmation-signin", attributes, response.getWriter());
//        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tokenAsString = request.getParameter("token");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        log.info("Sign in user with email {} and token {}", email, tokenAsString);
        Optional<User> optionalUser = userService.signInCheck(email, password, tokenAsString);
        response.setContentType("text/html;charset=utf-8");
        Map<String, String> attributes = new HashMap<>();
        log.debug("Received an optional with User with password verification by the passed " +
                "email address and password {'email':{}, 'optionalUser':{}}", email, optionalUser);
        if (optionalUser.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            attributes.put("token", tokenAsString);
            attributes.put("message", "Sorry, login or password is not correct, please check yours credentials and try again.");
            PageGenerator.getInstance().process("confirmation-signin", attributes, response.getWriter());
        } else {
            User user = optionalUser.get();
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(sessionMaxInactiveInterval);
            session.setAttribute("user", user);
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
            response.sendRedirect("/");
        }
    }

}
