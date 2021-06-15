package com.vinylteam.vinyl.web.servlets;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class SignOutServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            session.invalidate();
//        }
//        response.setStatus(HttpServletResponse.SC_OK);
//        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//        response.sendRedirect("/");
    }

}
