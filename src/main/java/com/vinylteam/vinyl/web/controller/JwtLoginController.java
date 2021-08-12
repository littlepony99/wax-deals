package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.vinylteam.vinyl.util.ControllerResponseUtils.getStatusInfoMap;
import static com.vinylteam.vinyl.util.ControllerResponseUtils.getSuccessStatusInfoMap;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "https://react-wax-deals.herokuapp.com"})
public class JwtLoginController {

    private final JwtService jwtTokenProvider;

    @GetMapping(value = "/successlogout", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> logout() {
        return new ResponseEntity<>(new HashMap<>(getSuccessStatusInfoMap()), OK);
    }

    @GetMapping(value = "/token", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> tokenChecking(@RequestHeader(name = "Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> credentialsMap = jwtTokenProvider.checkToken(token);
        if (!credentialsMap.isEmpty()) {
            response.putAll(credentialsMap);
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
            Map<String, Object> credentialsMap = jwtTokenProvider.authenticateByRequest(loginRequest);
            response.putAll(credentialsMap);
            response.putAll(getSuccessStatusInfoMap());
            return new ResponseEntity<>(response, CREATED);
        } catch (DisabledException e) {
            log.warn("User is not activated yet", e);
            response.putAll(getStatusInfoMap("1", "User is not activated yet"));
            return new ResponseEntity<>(response, BAD_REQUEST);
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

}
