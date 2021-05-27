package com.vinylteam.vinyl.web.filter;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        List<String> allowedUrls = List.of("/", "/catalog", "/search", "/oneVinyl", "/signIn", "/signUp",
                "/recoveryPassword", "/stores", "/contact", "/captcha", "/about");

        String uri = httpServletRequest.getRequestURI();

        if (allowedUrls.contains(uri) || uri.startsWith("/css") || uri.startsWith("/img") || uri.startsWith("/fonts")) {
            filterChain.doFilter(request, response);
        } else {
            HttpSession httpSession = httpServletRequest.getSession(false);
            if (httpSession != null) {
                User user = (User) httpSession.getAttribute("user");
                if (user != null) {
                    Role userRole = user.getRole();
                    if (EnumSet.of(Role.USER, Role.ADMIN).contains(userRole)) {
                        filterChain.doFilter(request, response);
                    } else {
                        httpServletResponse.sendRedirect("/signIn");
                    }
                } else {
                    httpServletResponse.sendRedirect("/signIn");
                }
            } else {
                httpServletResponse.sendRedirect("/signIn");
            }
        }
    }

}