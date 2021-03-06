package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.service.PasswordRecoveryService;
import com.vinylteam.vinyl.util.ControllerResponseUtils;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/password-recovery")
    public ResponseEntity<UserSecurityResponse> startPasswordRecoveryProcess(@RequestBody UserInfoRequest request) throws ServerException {
        UserSecurityResponse response = new UserSecurityResponse();
        passwordRecoveryService.sendLink(request.getEmail());
        ControllerResponseUtils.setSuccessStatusInfo(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/password-recovery")
    public ResponseEntity<UserSecurityResponse> finalizePasswordRecoveryProcess(@RequestBody UserInfoRequest newPasswordRequest) {
        UserSecurityResponse response = new UserSecurityResponse();
        passwordRecoveryService.changePassword(newPasswordRequest);
        ControllerResponseUtils.setSuccessStatusInfo(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/password-recovery/{recoveryToken}")
    public ResponseEntity<UserSecurityResponse> checkTokenRestEndPoint(@PathVariable(name = "recoveryToken") String token) {
        passwordRecoveryService.checkToken(token);
        UserSecurityResponse response = new UserSecurityResponse();
        ControllerResponseUtils.setSuccessStatusInfo(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/password-recovery")
    public ResponseEntity<UserSecurityResponse> checkToken(@RequestParam String token) {
        passwordRecoveryService.checkToken(token);
        UserSecurityResponse response = new UserSecurityResponse();
        ControllerResponseUtils.setSuccessStatusInfo(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
