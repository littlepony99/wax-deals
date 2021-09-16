
package com.vinylteam.vinyl.web.filter;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.EnumSet;

public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

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
