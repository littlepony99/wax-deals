package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.web.dto.ChangePasswordResponse;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;

import javax.servlet.http.HttpServletRequest;

public interface ProfileManagementService {

    UserSecurityResponse changeProfileAndReturnUser(HttpServletRequest request, UserInfoRequest userProfileInfo);

    ChangePasswordResponse changeProfilePassword(HttpServletRequest request, UserInfoRequest userProfileInfo);

    boolean deleteProfile(HttpServletRequest request, Long userId);
}
