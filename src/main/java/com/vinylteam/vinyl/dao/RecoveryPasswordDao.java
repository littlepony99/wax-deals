package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.RecoveryToken;

import java.util.Optional;

public interface RecoveryPasswordDao {

    boolean add(RecoveryToken recoveryToken);

    Optional<RecoveryToken> findByToken(String token);

    boolean deleteById(int id);

}