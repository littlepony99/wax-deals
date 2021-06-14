package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.web.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping(path = "/about")
public class AboutController {

    @GetMapping
    public String getAboutPage(HttpServletRequest request,
                               HttpServletResponse response,
                               Model model) {
        response.setContentType("text/html;charset=utf-8");
        log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_OK);
        WebUtils.setUserAttributes(request, model);
        return "about";
    }
}
