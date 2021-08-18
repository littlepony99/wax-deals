package com.vinylteam.vinyl.web.controller;

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
@CrossOrigin(origins = {"http://localhost:3000", "https://react-wax-deals.herokuapp.com"})
public class RestPasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/password-recovery")
    public ResponseEntity<UserSecurityResponse> startPasswordRecoveryProcess(@RequestBody UserInfoRequest request) {
        UserSecurityResponse response = new UserSecurityResponse();
        passwordRecoveryService.sendLink(request.getEmail());
        ControllerResponseUtils.setSuccessStatusInfo(response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/password-recovery")
    public ResponseEntity<UserSecurityResponse> finalizePasswordRecoveryProcess(@RequestBody UserInfoRequest newPasswordRequest) {
        UserSecurityResponse response = new UserSecurityResponse();
        passwordRecoveryService.changePassword(newPasswordRequest);
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
