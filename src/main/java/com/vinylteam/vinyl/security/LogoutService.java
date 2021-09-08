package com.vinylteam.vinyl.security;

import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;

public interface LogoutService extends LogoutHandler {

    void logout(HttpServletRequest request);

}
