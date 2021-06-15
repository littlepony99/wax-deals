package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.web.templater.PageGenerator;
import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AboutServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
//        Map<String, String> attributes = new HashMap<>();
//        WebUtils.setUserAttributes(request, attributes);
//        PageGenerator.getInstance().process("about", attributes, response.getWriter());
    }

}