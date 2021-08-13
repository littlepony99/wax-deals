package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.ConfirmationToken;

import java.util.Optional;
import java.util.UUID;

public interface EmailConfirmationService {

    Optional<ConfirmationToken> findByToken(String token);

    Optional<ConfirmationToken> findByUserId(long userId);

    ConfirmationToken addByUserId(long userId);

    UUID generateConfirmationToken();

    void update(ConfirmationToken confirmationToken);

    void sendMessageWithLinkToUserEmail(String email, String token);

    void deleteByUserId(long userId);

}
