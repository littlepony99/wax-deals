package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.security.LogoutService;
import com.vinylteam.vinyl.service.JwtService;
import com.vinylteam.vinyl.service.ProfileManagementService;
import com.vinylteam.vinyl.web.dto.ChangePasswordResponse;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/profile", produces = "text/html;charset=UTF-8")
public class ProfileController {

    private final UserMapper userMapper;
    private final ProfileManagementService profileService;
    private final JwtService jwtService;
    private LogoutService logoutService;

    @PutMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserSecurityResponse> submitProfileChanges(HttpServletRequest request, @RequestBody UserInfoRequest userProfileInfo) {
        return new ResponseEntity<>(profileService.changeProfileAndReturnUser(request, userProfileInfo), OK);
    }

    @PutMapping(path = "/change-password", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ChangePasswordResponse> changePassword(HttpServletRequest request, @RequestBody UserInfoRequest userProfileInfo) {
        return new ResponseEntity<>(profileService.changeProfilePassword(request, userProfileInfo), OK);
    }

    @DeleteMapping(path = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> deleteUserProfile(HttpServletRequest request, @PathVariable("userId") String userId) {
        if (profileService.deleteProfile(request, Long.valueOf(userId))) {
            return ResponseEntity.ok("Profile has been successfully deleted");
        } else {
            return ResponseEntity.badRequest().body("User is not allowed to manage someone else`s profile");
        }
    }

}
