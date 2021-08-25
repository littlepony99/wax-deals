package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.security.LogoutService;
import com.vinylteam.vinyl.service.JwtTokenProvider;
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

    private final UserService userService;
    private final LogoutService logoutService;
    private final JwtTokenProvider jwtService;
    private final UserMapper userMapper;

    @PutMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDto> submitProfileChanges(HttpServletRequest request, @RequestBody UserInfoRequest userProfileInfo) {
        var userAfterEdit = Optional.ofNullable((User) request.getAttribute("userEntity"))
                .map(foundUser -> userService.changeDiscogsUserName(userProfileInfo.getDiscogsUserName(), foundUser))
                .get();
        return new ResponseEntity<>(userMapper.mapUserToDto(userAfterEdit), OK);
    }

    @PutMapping(path = "/change-password", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ChangePasswordResponse> changePassword(HttpServletRequest request, @RequestBody UserInfoRequest userProfileInfo) {
        User user = (User) request.getAttribute("userEntity");
        userService.changeUserPassword(userProfileInfo, user);
        var authResponse = jwtService.authenticateByRequest(new LoginRequest(user.getEmail(), userProfileInfo.getNewPassword()));
        String token = authResponse.getToken();
        logoutService.logout(request, null, null);
        var response = new ChangePasswordResponse("Your password has been changed.", token);
        return new ResponseEntity<>(response, OK);
    }

    @DeleteMapping(path = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<String> deleteUserProfile(HttpServletRequest request, @PathVariable("userId") String userId) {
        User currentUser = (User) request.getAttribute("userEntity");
        if (currentUser.getId().equals(Long.valueOf(userId))) {
            userService.delete(currentUser);
            logoutService.logout(request, null, null);
            return ResponseEntity.ok("Profile has been successfully deleted");
        } else {
            return ResponseEntity.badRequest().body("User is not allowed to manage someone else`s profile");
        }
    }

}
