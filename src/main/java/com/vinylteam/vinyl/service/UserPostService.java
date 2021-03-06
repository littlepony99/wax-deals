package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.web.dto.AddUserPostDto;

public interface UserPostService {

    void processAdd(UserPost post) throws ServerException;

    void addUserPostWithCaptchaRequest(AddUserPostDto dto) throws ForbiddenException, ServerException;

}