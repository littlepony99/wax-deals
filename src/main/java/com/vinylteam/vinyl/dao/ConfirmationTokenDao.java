package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.ConfirmationToken;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenDao {

    Optional<ConfirmationToken> findByToken(UUID token);

    Optional<ConfirmationToken> findByUserId(long userId);

    void add(ConfirmationToken token);

    void update(ConfirmationToken token);

    void deleteByUserId(long userId);

}
