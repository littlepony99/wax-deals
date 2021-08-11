package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.User;
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
@CrossOrigin(origins = {"http://localhost:3000", "http://react-wax-deals.herokuapp.com"})
public class RestSignupController {

    private final UserService userService;
    private final EmailConfirmationService emailConfirmationService;
    private final UserMapper userMapper;
    private final JwtService jwtService;


    @PostMapping("/signUp")
    public ResponseEntity<Map<String, Object>> signUpUser(@RequestBody UserInfoRequest userProfileInfo) {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            userService.register(userProfileInfo);
            log.debug("User was added with " +
                    "passed email and password to db {'email':{}}", userProfileInfo.getEmail());
            responseMap.putAll(getStatusInfoMap("0", "Please confirm your registration. To do this, follow the link that we sent to your email - " + userProfileInfo.getEmail()));
            ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseMap, HttpStatus.SEE_OTHER);
            log.debug("Set response status to {'status':{}}", HttpStatus.SEE_OTHER);
            return response;
        } catch (Exception e) {
            log.error("Error during registration", e);
            responseMap.putAll(getStatusInfoMap("1", e.getMessage()));
            ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            return response;
        }
    }

    @PostMapping("/emailConfirmation")
    public ResponseEntity<Map<String, Object>> getConfirmationResponse(@RequestBody UserInfoRequest request) {
        var token = request.getToken();
        Map<String, Object> responseMap = new HashMap<>();
        JwtUser user = getUserWhoNeedsConfirmation(token);
        UserInfoRequest userProfileInfo = request;
        userProfileInfo.setEmail(user.getUsername());
        log.info("Sign in user with email {} and token {}", user.getUsername(), token);
        try {
            JwtUser confirmedUser = userMapper.mapToDto(userService.confirmEmail(userProfileInfo));

            responseMap.putAll(getUserCredentialsMap(jwtService.createToken(confirmedUser.getUsername(), user.getAuthorities()), user));
            responseMap.putAll(getStatusInfoMap("0", ""));
            log.debug("Set response status to {'status':{}}", HttpStatus.OK);
            ResponseEntity response = new ResponseEntity(responseMap, HttpStatus.OK);
            return response;
        } catch (Exception e){
            log.error("Error during confirmation ", e);
            responseMap.putAll(getStatusInfoMap("1", e.getMessage()));
            ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            return response;
        }
    }


    private Map<String, Object> getUserCredentialsMap(String token, JwtUser authUser) {
        String username = authUser.getUsername();
        User byEmail = userService.findByEmail(username);
        return Map.of(
                "user", userMapper.mapUserToDto(byEmail),
                "token", token);
    }

    private JwtUser getUserWhoNeedsConfirmation(String tokenAsString) {
        JwtUser user = emailConfirmationService
                .findByToken(tokenAsString)
                .map(foundConfirmationToken -> foundConfirmationToken.getUserId())
                .flatMap(userFromToken -> userService.findById(userFromToken))
                .map(foundUser -> userMapper.mapToDto(foundUser))
                .get();
        return user;
    }

    private Map<String, String> getStatusInfoMap(String code, String s) {
        return Map.of(
                "resultCode", code,
                "message", s);
    }

}
