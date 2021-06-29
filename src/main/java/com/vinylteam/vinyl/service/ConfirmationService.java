package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationService {

    Optional<ConfirmationToken> findByToken(String token);

    Optional<ConfirmationToken> findByUserId(long userId);

    ConfirmationToken addByUserId(long userId);

    void update(ConfirmationToken confirmationToken);

    void sendMessageWithLinkToUserEmail(String email, String token);

    void deleteByUserId(long userId);

}
