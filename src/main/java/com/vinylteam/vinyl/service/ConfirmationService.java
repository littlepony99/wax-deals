package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.ConfirmationToken;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationService {

    Optional<ConfirmationToken> findByToken(UUID token);

    Optional<ConfirmationToken> findByUserId(long userId);

    ConfirmationToken addByUserId(long userId);

    boolean update(ConfirmationToken confirmationToken);

    boolean sendMessageWithLinkToUserEmail(String email, String token);

    boolean deleteByUserId(long userId);

}
