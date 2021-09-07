package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.exception.JwtAuthenticationException;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    public ResponseEntity<UserSecurityResponse> checkToken(@RequestHeader(name = "Authorization") String token) {
        UserSecurityResponse responseObject = jwtTokenProvider.getCheckResponseIfTokenValid(token);
        if (responseObject.getUser() != null) {
            return new ResponseEntity<>(setSuccessStatusInfo(responseObject), OK);
        } else {
            responseObject = setStatusInfo(responseObject, "1", "Token is expired");
            return new ResponseEntity<>(responseObject, ACCEPTED);
        }
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserSecurityResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserSecurityResponse responseObject = jwtTokenProvider.authenticateByRequest(loginRequest);
            return new ResponseEntity<>(setSuccessStatusInfo(responseObject), OK);
        } catch (DisabledException e) {
            log.warn("User is not activated yet", e);
            return new ResponseEntity<>(setStatusInfo(new UserSecurityResponse(), "1", "Your email isn't confirmed. Check your mailbox for the confirmation link"), BAD_REQUEST);
        } catch (AuthenticationException e) {
            log.warn("Invalid username or password", e);
            return new ResponseEntity<>(setStatusInfo(new UserSecurityResponse(), "1", "Invalid username or password"), BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error during login attempt, user {}", loginRequest, e);
            return new ResponseEntity<>(setStatusInfo(new UserSecurityResponse(), "1", e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/token/refresh-token", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserSecurityResponse> refresh(HttpServletRequest request, @RequestBody Map<String, String> parameters) {
        String refreshToken = parameters.get("refreshToken");
        jwtTokenProvider.checkJwtAuthorization(request, refreshToken);
        try {
            UserSecurityResponse response = jwtTokenProvider.refreshByToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (JwtAuthenticationException e){
            log.error("Error: ", e);
            return new ResponseEntity<>(setStatusInfo(new UserSecurityResponse(), "1", "Refresh token is expired."), FORBIDDEN);
        } catch (Exception e) {
            log.error("Unexpected error during token refresh attempt", e);
            return new ResponseEntity<>(setStatusInfo(new UserSecurityResponse(), "1", e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

}
