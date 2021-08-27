package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.web.dto.ChangePasswordResponse;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;

import javax.servlet.http.HttpServletRequest;

public interface ProfileManagementService {

    User changeProfileAndReturnUser(HttpServletRequest request, UserInfoRequest userProfileInfo);

    ChangePasswordResponse changeProfilePassword(HttpServletRequest request, UserInfoRequest userProfileInfo);

    boolean deleteProfile(HttpServletRequest request, Long userId);
}
