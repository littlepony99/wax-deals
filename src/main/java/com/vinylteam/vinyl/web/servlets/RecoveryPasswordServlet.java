package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.exception.RecoveryPasswordException;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class RecoveryPasswordServlet extends HttpServlet {

    private final RecoveryPasswordService recoveryPasswordService;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_OK);
//        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//        Map<String, String> attributes = new HashMap<>();
//        WebUtils.setUserAttributes(request, attributes);
//        PageGenerator.getInstance().process("recoveryPassword", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Map<String, String> attributes = new HashMap<>();
//        response.setContentType("text/html;charset=utf-8");
//        String email = request.getParameter("email");
//        attributes.put("email", email);
//        WebUtils.setUserAttributes(request, attributes);
//        try {
//            recoveryPasswordService.sendLink(email);
//            log.debug("Successfully send mail for recovery password to email - {'email':{}}", email);
//            response.setStatus(HttpServletResponse.SC_OK);
//            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//            attributes.put("message", "Follow the link that we sent you by email - " + email);
//        } catch (RecoveryPasswordException e) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
//            log.error(e.getMessage());
//            attributes.put("message", e.getMessage());
//        }
//        PageGenerator.getInstance().process("recoveryPassword", attributes, response.getWriter());
    }

}
