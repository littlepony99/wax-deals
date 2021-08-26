package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.LogoutService;
import com.vinylteam.vinyl.service.impl.JwtTokenProvider;
import com.vinylteam.vinyl.web.dto.ChangePasswordResponse;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultProfileManagementService implements ProfileManagementService {

    private final UserService userService;
    private final LogoutService logoutService;
    private final JwtTokenProvider jwtService;

    @Override
    public User changeDiscogsUserNameAndReturnUser(HttpServletRequest request, UserInfoRequest userProfileInfo) {
        var userAfterEdit = Optional.ofNullable((User) request.getAttribute("userEntity"))
                .map(foundUser -> userService.changeDiscogsUserName(foundUser, userProfileInfo.getDiscogsUserName()))
                .get();
        return userAfterEdit;
    }

    @Override
    public ChangePasswordResponse changeProfilePassword(HttpServletRequest request, UserInfoRequest userProfileInfo) {
        User user = (User) request.getAttribute("userEntity");
        userService.changeUserPassword(userProfileInfo, user);
        String token = jwtService
                .authenticateByRequest(new LoginRequest(user.getEmail(), userProfileInfo.getNewPassword()))
                .getToken();
        logoutService.logout(request, null, null);
        return new ChangePasswordResponse("Your password has been changed.", token);
    }

    @Override
    public boolean deleteProfile(HttpServletRequest request, Long userId) {
        User currentUser = (User) request.getAttribute("userEntity");
        if (!currentUser.getId().equals(userId)) {
            return false;
        }
        userService.delete(currentUser);
        logoutService.logout(request, null, null);
        return true;
    }
}
