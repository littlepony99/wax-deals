package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.JwtTokenProvider;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JwtLoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpResponse) {
        var userHolder = Optional.ofNullable((JwtUser) userDetailsService.loadUserByUsername(loginRequest.getEmail()));
        if (userHolder.isEmpty()) {
            throw new BadCredentialsException("Invalid username or password");
        }
        var user = userHolder.get();
        try {
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword());
            authenticationManager.authenticate(auth);
            String token = jwtTokenProvider.createToken(user.getUsername(), user.getAuthorities());
            User originalUser = userService.findByEmail(user.getUsername());
            Map<Object, Object> response = Map.of(
                    "user", originalUser,
                    "token", token);
            return new ResponseEntity<>(response, CREATED);
        } catch (AuthenticationException e) {
            log.error("Invalid username or password", e);
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
