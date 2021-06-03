package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface ConfirmationService {

    Optional<ConfirmationToken> findByToken(UUID token);

    Optional<ConfirmationToken> findByUserId(long userId);

    boolean addByUserId(long userId);

    boolean update(ConfirmationToken confirmationToken);

    boolean sendMessageWithLinkToUserEmail(User user);

    boolean deleteByUserId(long userId);

}
