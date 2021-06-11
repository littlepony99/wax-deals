package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.RecoveryToken;

import java.util.Optional;
import java.util.UUID;

public interface RecoveryPasswordDao {

    boolean add(RecoveryToken recoveryToken);

    Optional<RecoveryToken> findByToken(UUID token);

    boolean deleteById(long id);

}
