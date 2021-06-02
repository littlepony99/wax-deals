package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.UserPost;

public interface UserPostDao {

    boolean add(UserPost post);

    boolean updateRecoveryUserToken(long userId, String recoveryToken);

    boolean addRecoveryUserToken(long userId, String recoveryToken);

    String getRecoveryUserToken(long userId);

    long getRecoveryUserId(String token);

}