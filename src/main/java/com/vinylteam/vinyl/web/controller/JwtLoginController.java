package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://react-wax-deals.herokuapp.com"})
public class JwtLoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtTokenProvider;
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping(value = "/successlogout", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(name = "Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        response.putAll(getSuccessStatusInfoMap());

        return new ResponseEntity<>(response, OK);
    }

    @GetMapping(value = "/token", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> tokenChecking(@RequestHeader(name = "Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        if (jwtTokenProvider.validateToken(token)) {
            var auth = jwtTokenProvider.getAuthentication(token);
            var authUser = (JwtUser) auth.getPrincipal();
            response.putAll(getUserCredentialsMap(token, authUser));
            response.putAll(Map.of("token", token));
            response.putAll(getSuccessStatusInfoMap());
            return new ResponseEntity<>(response, OK);
        } else {
            response.putAll(getStatusInfoMap("1", "Token is not valid"));
            return new ResponseEntity<>(response, ACCEPTED);
        }
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication preparedAuth = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(preparedAuth);
            var authUser = (JwtUser) authentication.getPrincipal();
            String token = jwtTokenProvider.createToken(authUser.getUsername(), authUser.getAuthorities());
            response.putAll(getUserCredentialsMap(token, authUser));
            response.putAll(getSuccessStatusInfoMap());
            return new ResponseEntity<>(response, CREATED);
        } catch (AuthenticationException e) {
            log.warn("Invalid username or password", e);
            response.putAll(getStatusInfoMap("1", "Invalid username or password"));
            return new ResponseEntity<>(response, BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error during login attempt, user {}", loginRequest, e);
            response.putAll(getStatusInfoMap("1", e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, String> getSuccessStatusInfoMap() {
        return getStatusInfoMap("0", "");
    }

    private Map<String, String> getStatusInfoMap(String code, String s) {
        return Map.of(
                "resultCode", code,
                "message", s);
    }

    private Map<String, Object> getUserCredentialsMap(String token, JwtUser authUser) {
        String username = authUser.getUsername();
        User byEmail = userService.findByEmail(username);
        return Map.of(
                "user", userMapper.mapUserToDto(byEmail),
                "token", token);
    }

}
