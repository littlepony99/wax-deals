package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.RecoveryToken;

import java.util.Optional;

public interface RecoveryPasswordDao {

    boolean addRecoveryUserToken(RecoveryToken recoveryToken);

    Optional<RecoveryToken> getByRecoveryToken(String token);

    boolean removeRecoveryUserToken(String token);

}
