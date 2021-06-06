package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.RecoveryPasswordDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.service.UserService;

import java.util.Optional;
import java.util.UUID;

public class DefaultRecoveryPasswordService implements RecoveryPasswordService {

    private final RecoveryPasswordDao recoveryPasswordDao;
    private final UserService userService;

    public DefaultRecoveryPasswordService(RecoveryPasswordDao recoveryPasswordDao, UserService userService) {
        this.recoveryPasswordDao = recoveryPasswordDao;
        this.userService = userService;
    }

    @Override
    public String addRecoveryUserToken(long userId) {
        boolean isAdded;
        String token = UUID.randomUUID().toString();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setUserId(userId);
        recoveryToken.setToken(token);
        isAdded = recoveryPasswordDao.addRecoveryUserToken(recoveryToken);
        if (!isAdded) {
            token = "";
        }
        return token;
    }

    @Override
    public Optional<RecoveryToken> getByRecoveryToken(String token) {
        return recoveryPasswordDao.getByRecoveryToken(token);
    }

    @Override
    public boolean removeRecoveryUserToken(String token) {
        boolean isRemoved = recoveryPasswordDao.removeRecoveryUserToken(token);
        return isRemoved;
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userService.getByEmail(email);
    }

    @Override
    public Optional<User> findById(long userId) {
        return userService.findById(userId);
    }

    @Override
    public boolean update(String oldEmail, String newEmail, String newPassword, String discogsUserName) {
        return userService.update(oldEmail, newEmail, newPassword, discogsUserName);
    }
}
