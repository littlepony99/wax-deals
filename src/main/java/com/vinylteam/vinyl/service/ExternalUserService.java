package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface ExternalUserService {

    UserSecurityResponse processExternalAuthorization(String token) throws GeneralSecurityException, IOException, ServerException;

}
