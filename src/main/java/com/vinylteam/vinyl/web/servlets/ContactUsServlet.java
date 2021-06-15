package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.service.CaptchaService;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ContactUsServlet extends HttpServlet {

    private final UserPostService userPostService;
    private final CaptchaService defaultCaptchaService;

    public ContactUsServlet(UserPostService service, CaptchaService defaultCaptchaService) {
        this.userPostService = service;
        this.defaultCaptchaService = defaultCaptchaService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//
//        Map<String, String> attributes = new HashMap<>();
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            User user = (User) session.getAttribute("user");
//            if (user != null) {
//                attributes.put("userRole", user.getRole().toString());
//            }
//        }
//        PageGenerator.getInstance().process("contactUs", attributes, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String messageContactUs = request.getParameter("messageContactUs");
        String captcha = request.getParameter("captcha");

        boolean isCaptchaValid = defaultCaptchaService.validateCaptcha(request.getSession().getId(), captcha);

        if (isCaptchaValid) {
            UserPost post = new UserPost(name, email, subject, messageContactUs, LocalDateTime.now());
            boolean isPostProcessed = userPostService.processAdd(post);
            if (isPostProcessed) {
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Post added");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.info("Post not added");
            }
            response.sendRedirect("/");
        } else {
            Map<String, String> attributes = new HashMap<>();
            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    attributes.put("userRole", user.getRole().toString());
                }
            }
            attributes.put("captchaError", "Captcha is invalid!!!");
            PageGenerator.getInstance().process("contactUs", attributes, response.getWriter());
        }
    }

}