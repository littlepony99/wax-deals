package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.RecoveryToken;

import java.util.Optional;
import java.util.UUID;

public interface PasswordRecoveryDao {

    void add(RecoveryToken recoveryToken);

    Optional<RecoveryToken> findByToken(UUID token);

    void deleteById(long id);

}
