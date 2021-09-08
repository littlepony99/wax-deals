package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.DefaultLogoutService;
import com.vinylteam.vinyl.service.impl.JwtTokenProvider;
import com.vinylteam.vinyl.util.ControllerResponseUtils;
import com.vinylteam.vinyl.web.dto.ChangePasswordResponse;
import com.vinylteam.vinyl.web.dto.LoginRequest;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class DefaultProfileManagementService implements ProfileManagementService {

    private final UserService userService;
    private final DefaultLogoutService logoutService;
    private final JwtTokenProvider jwtService;
    private final UserMapper userMapper;

    @Override
    public UserSecurityResponse changeProfileAndReturnUser(HttpServletRequest request, UserInfoRequest userProfileInfo) {
        User user = (User) request.getAttribute("userEntity");
        String oldEmail = user.getEmail();

        userService.changeProfile(user, userProfileInfo.getEmail(), userProfileInfo.getDiscogsUserName());
        var response = ControllerResponseUtils.getResponseWithMessage("Your email and/or discogs username have been changed.");
        if (!user.getEmail().equals(oldEmail)) {
            var newTokenPair = jwtService.getTokenPair(userMapper.mapToDto(user));
            response.setJwtToken(newTokenPair.getJwtToken());
            response.setRefreshToken(newTokenPair.getRefreshToken());
            logoutService.logout(request, null, null);
        }
        return response;
    }

    @Override
    public ChangePasswordResponse changeProfilePassword(HttpServletRequest request, UserInfoRequest userProfileInfo) {
        User user = (User) request.getAttribute("userEntity");
        userService.changeUserPassword(userProfileInfo, user);
        String token = jwtService
                .authenticateByRequest(new LoginRequest(user.getEmail(), userProfileInfo.getNewPassword()))
                .getJwtToken();
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
