package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.LogoutService;
import com.vinylteam.vinyl.service.ProfileManagementService;
import com.vinylteam.vinyl.service.impl.JwtTokenProvider;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.ChangePasswordResponse;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UserDto;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/profile", produces = "text/html;charset=UTF-8")
public class ProfileController {

    private final UserMapper userMapper;
    private final ProfileManagementService profileService;

    @PutMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDto> submitProfileChanges(HttpServletRequest request, @RequestBody UserInfoRequest userProfileInfo) {
        User userAfterEdit = profileService.changeDiscogsUserNameAndReturnUser(request, userProfileInfo);
        return new ResponseEntity<>(userMapper.mapUserToDto(userAfterEdit), OK);
    }


    @PutMapping(path = "/change-password", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ChangePasswordResponse> changePassword(HttpServletRequest request, @RequestBody UserInfoRequest userProfileInfo) {
        ChangePasswordResponse response = profileService.changeProfilePassword(request, userProfileInfo);
        return new ResponseEntity<>(response, OK);
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
