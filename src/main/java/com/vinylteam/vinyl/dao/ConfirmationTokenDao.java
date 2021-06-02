package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.ConfirmationToken;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenDao {

    Optional<ConfirmationToken> findByToken(UUID token);

    Optional<ConfirmationToken> findByUserId(long userId);

    boolean add(ConfirmationToken token);

    boolean deleteByUserId(long userId);

}
