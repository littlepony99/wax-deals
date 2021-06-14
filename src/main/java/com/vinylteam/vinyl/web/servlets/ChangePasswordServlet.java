package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.exception.RecoveryPasswordException;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.web.templater.PageGenerator;
import com.vinylteam.vinyl.web.util.WebUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ChangePasswordServlet extends HttpServlet {

    private final RecoveryPasswordService recoveryPasswordService;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        Map<String, String> attributes = new HashMap<>();
//        WebUtils.setUserAttributes(request, attributes);
//        String token = request.getParameter("token");
//        try {
//            recoveryPasswordService.checkToken(token);
//            response.setStatus(HttpServletResponse.SC_OK);
//            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//            attributes.put("recoveryToken", token);
//        } catch (RecoveryPasswordException e) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            log.debug("Set response status to {'status':{}}, error - {}", HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
//            attributes.put("message", e.getMessage());
//        }
//        PageGenerator.getInstance().process("newPassword", attributes, response.getWriter());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        Map<String, String> attributes = new HashMap<>();
//        WebUtils.setUserAttributes(request, attributes);
//        String newPassword = request.getParameter("password");
//        String confirmPassword = request.getParameter("confirmPassword");
//        String token = request.getParameter("recoveryToken");
//        try {
//            recoveryPasswordService.changePassword(newPassword, confirmPassword, token);
//            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
//            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
//            attributes.put("message", "Your password was changed. Please, try to log in use new password.");
//        } catch (RecoveryPasswordException e) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            log.debug("Set response status to {'status':{}}, error - {}", HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
//            attributes.put("message", e.getMessage());
//        }
//        PageGenerator.getInstance().process("signIn", attributes, response.getWriter());
    }

}
