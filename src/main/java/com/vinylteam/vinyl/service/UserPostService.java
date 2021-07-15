package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.web.dto.CaptchaRequestDto;
import org.springframework.transaction.annotation.Transactional;

public interface UserPostService {

    void processAdd(UserPost post);

    @Transactional
    Boolean processRequest(CaptchaRequestDto dto) throws ForbiddenException;
}