package com.vinylteam.vinyl.web.filter;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class UserAttributeFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return false;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<User> sessionUser = Optional.ofNullable((User) request.getSession().getAttribute("user"));
        if (!sessionUser.isPresent()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                UserDetails principal = (UserDetails) authentication.getPrincipal();
                if (principal != null) {
                    var foundUser = (User) userService.findByEmail(principal.getUsername());
                    request.getSession().setAttribute("user", foundUser);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
