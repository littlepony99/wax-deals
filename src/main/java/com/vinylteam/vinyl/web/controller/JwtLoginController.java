package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import static com.vinylteam.vinyl.util.ControllerResponseUtils.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JwtLoginController {

    private final JwtService jwtTokenProvider;

    @GetMapping(value = "/successlogout", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserSecurityResponse> logout() {
        return new ResponseEntity<>(setSuccessStatusInfo(new UserSecurityResponse()), OK);
    }

    @GetMapping(value = "/token", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserSecurityResponse> tokenChecking(@RequestHeader(name = "Authorization") String token) {
        UserSecurityResponse responseObject = jwtTokenProvider.getCheckResponseIfTokenValid(token);
        if (responseObject.getUser() != null) {
            return new ResponseEntity<>(setSuccessStatusInfo(responseObject), OK);
        } else {
            responseObject = setStatusInfo(responseObject, "1", "Token is not valid");
            return new ResponseEntity<>(responseObject, ACCEPTED);
        }
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserSecurityResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserSecurityResponse responseObject = jwtTokenProvider.authenticateByRequest(loginRequest);
            return new ResponseEntity<>(setSuccessStatusInfo(responseObject), CREATED);
        } catch (DisabledException e) {
            log.warn("User is not activated yet", e);
            return new ResponseEntity<>(setStatusInfo(new UserSecurityResponse(), "1", "User is not activated yet"), BAD_REQUEST);
        } catch (AuthenticationException e) {
            log.warn("Invalid username or password", e);
            return new ResponseEntity<>(setStatusInfo(new UserSecurityResponse(), "1", "Invalid username or password"), BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error during login attempt, user {}", loginRequest, e);
            return new ResponseEntity<>(setStatusInfo(new UserSecurityResponse(), "1", e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

}
