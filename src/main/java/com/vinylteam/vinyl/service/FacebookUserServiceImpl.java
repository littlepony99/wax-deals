package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class FacebookUserServiceImpl implements ExternalUserService {
    @Override
    public UserSecurityResponse processExternalAuthorization(String token) throws GeneralSecurityException, IOException, ServerException {
        return null;
    }
}
