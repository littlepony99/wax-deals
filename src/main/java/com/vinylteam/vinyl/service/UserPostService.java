package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UserPost;

public interface UserPostService {

    boolean processAdd(UserPost post);

    boolean add(UserPost post);

    String addRecoveryUserToken(long userId);

    long getRecoveryUserId(String token);
}