package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.MailSender;
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
public class RecoveryPasswordServlet extends HttpServlet {

    private static final String RECOVERY_MESSAGE = "Hello, to change your password, follow this link: \nhttp://localhost:8080/newPassword?token=";

    private final UserService userService;
    private final UserPostService userPostService;
    private final MailSender mailSender;

    public RecoveryPasswordServlet(UserService userService, UserPostService userPostService, MailSender mailSender) {
        this.userService = userService;
        this.userPostService = userPostService;
        this.mailSender = mailSender;
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
            }
        }
        PageGenerator.getInstance().process("recoveryPassword", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> attributes = new HashMap<>();
        response.setContentType("text/html;charset=utf-8");
        String email = request.getParameter("email");
        attributes.put("email", email);
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                attributes.put("userRole", user.getRole().toString());
            }
        }
        if (email != null && !email.isEmpty()){
            Optional<User> user = userService.getByEmail(email);
            if (user.isPresent()){
                long userId = user.get().getId();
                String recoveryToken = userPostService.addRecoveryUserToken(userId);
                if (!recoveryToken.isEmpty()){
                    String recoveryLink = RECOVERY_MESSAGE + recoveryToken;
                    String recoveryTopic = "Recovery Password - WaxDeals";
                    boolean isSent = mailSender.sendMail(email, recoveryTopic, recoveryLink);
                    if (isSent){
                        log.debug("Successfully send mail for recovery password to email - {'email':{}}", email);
                        response.setStatus(HttpServletResponse.SC_OK);
                        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
                        attributes.put("message", "Please confirm your email. To do this, follow the link that we sent you by email - " + email);
                    } else {
                        log.debug("Failed send mail for recovery password to email - {'email':{}}", email);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                        attributes.put("message", "Something went wrong. Please contact support.");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                    log.debug("Failed create and add recovery user token to db {'userId':{}}.", userId);
                    attributes.put("message", "We can't recover your password by this email. Please check your email or contact us.");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                log.debug("Failed create and add recovery user token to db {'email':{}}.", email);
                attributes.put("message", "We can't find matching email. Please check your email or contact us.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            log.debug("Error. Email is empty {'email':{}}.", email);
            attributes.put("message", "Error. Email is empty. Please enter email correctly.");
        }
        PageGenerator.getInstance().process("recoveryPassword", attributes, response.getWriter());
    }
}
