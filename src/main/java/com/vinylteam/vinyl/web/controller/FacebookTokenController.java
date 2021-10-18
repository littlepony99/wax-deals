package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.service.ExternalUserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/facebook")
@Slf4j
public class FacebookTokenController {

    private final ExternalUserService facebookService;

    public FacebookTokenController(@Qualifier("facebookUserService")ExternalUserService facebookService) {
        this.facebookService = facebookService;
    }

    @PostMapping("/auth")
    public ResponseEntity<UserSecurityResponse> loginByGoogle(@RequestBody UserInfoRequest request) throws GeneralSecurityException, IOException, ServerException {
        var response = facebookService.processExternalAuthorization(request.getToken());
        if (!Objects.isNull(response.getUser())) {
            return ResponseEntity.ok(response);
        } else {
            log.info("Invalid ID token {}.", request.getToken());
            response.setMessage("Invalid ID token.");
            return ResponseEntity.status(BAD_REQUEST).body(response);
        }
    }
}
