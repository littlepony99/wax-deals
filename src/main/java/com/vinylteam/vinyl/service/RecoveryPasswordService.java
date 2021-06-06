package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;

import java.util.Optional;

public interface RecoveryPasswordService {

    String addRecoveryUserToken(long userId);

    Optional<RecoveryToken> getByRecoveryToken(String token);

    boolean removeRecoveryUserToken(String token);

    Optional<User> getByEmail(String email);

    Optional<User> findById(long userId);

    boolean update(String oldEmail, String newEmail, String newPassword, String discogsUserName);
}
