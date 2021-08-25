package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.service.EmailConfirmationService;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SignupController {

    private final UserService userService;
    private final EmailConfirmationService emailConfirmationService;
    private final UserMapper userMapper;
    private final JwtService jwtService;


    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, Object>> signUpUser(@RequestBody UserInfoRequest userProfileInfo) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            userService.register(userProfileInfo);
            log.debug("User was added with " +
                    "passed email and password to db {'email':{}}", userProfileInfo.getEmail());
            responseMap.putAll(getMessageMap("In order to confirm your email click on the confirmation link we sent to your mailbox. Might be in \"spam\"!"));
            ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseMap, HttpStatus.OK);
            log.debug("Set response status to {'status':{}}", HttpStatus.OK);
            return response;
        } catch (ServerException e) {
            log.error("Error during sending confirmation email", e);
            responseMap.putAll(getMessageMap(e.getMessage()));
            ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
            log.debug("Set response status to {'status':{}}", HttpStatus.INTERNAL_SERVER_ERROR);
            return response;
        } catch (RuntimeException e) {
            log.error("Error during registration", e);
            responseMap.putAll(getMessageMap(e.getMessage()));
            ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpStatus.BAD_REQUEST);
            return response;
        }
    }

    @PutMapping("/email-confirmation")
    public ResponseEntity<Map<String, Object>> getConfirmationResponse(@RequestParam(value = "confirmToken") String token) {
        Map<String, Object> responseMap = new HashMap<>();
        userService.confirmEmailByToken(token);
        responseMap.putAll(getMessageMap("Your email is confirmed. Now you can log in."));
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseMap, HttpStatus.OK);
        log.debug("Set response status to {'status':{}}", HttpStatus.OK);
        return response;
    }


    private Map<String, Object> getUserCredentialsMap(String token, JwtUser authUser) {
        String username = authUser.getUsername();
        User byEmail = userService.findByEmail(username);
        return Map.of(
                "user", userMapper.mapUserToDto(byEmail),
                "token", token);
    }

    private Map<String, String> getMessageMap(String s) {
        return Map.of(
                "message", s);
    }

}
