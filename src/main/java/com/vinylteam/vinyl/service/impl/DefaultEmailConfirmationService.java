package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.exception.entity.EmailConfirmationErrors;
import com.vinylteam.vinyl.service.EmailConfirmationService;
import com.vinylteam.vinyl.util.MailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultEmailConfirmationService implements EmailConfirmationService {
    private static final String SUBJECT = "Confirm your email on wax-deals.com";
    private static final String MAIL_BODY_BEGINNING = "To confirm click on the link below and log into your account.\n";
    private static final String LINK_PATH = "/emailConfirmation?token=";
    private static final String MAIL_BODY_ENDING = "\n Thank you!\n\nWith kindest regards,\nWax-Deals team";

    private final ConfirmationTokenDao confirmationTokenDao;
    private final MailSender mailSender;

    @Value("${application.link}")
    private String applicationLink;

    @Override
    public Optional<ConfirmationToken> findByToken(String token) {
        try {
            UUID tokenUUID = UUID.fromString(token);
            ConfirmationToken confirmationToken = confirmationTokenDao.findByToken(tokenUUID)
                    .orElseThrow(() -> new RuntimeException(EmailConfirmationErrors.TOKEN_FROM_LINK_NOT_FOUND.getMessage()));
            return Optional.of(confirmationToken);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(EmailConfirmationErrors.TOKEN_FROM_LINK_NOT_UUID.getMessage());
        }
    }

    @Override
    public Optional<ConfirmationToken> findByUserId(long userId) {
        ConfirmationToken confirmationToken = confirmationTokenDao.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException(EmailConfirmationErrors.TOKEN_FOR_USER_ID_NOT_FOUND.getMessage()));
        return Optional.of(confirmationToken);
    }

    @Override
    public ConfirmationToken addByUserId(long userId) {
        ConfirmationToken newConfirmationToken = ConfirmationToken.builder()
                .userId(userId)
                .token(generateConfirmationToken())
                .build();
        log.info("Generated token: {}", newConfirmationToken.getToken());
        confirmationTokenDao.add(newConfirmationToken);
        return newConfirmationToken;
    }

    @Override
    public UUID generateConfirmationToken() {
        UUID uuid = UUID.randomUUID();
        return uuid;
    }

    @Override
    public void update(ConfirmationToken confirmationToken) {
        if (confirmationToken == null) {
            throw new RuntimeException(EmailConfirmationErrors.CAN_NOT_CREATE_LINK_TRY_AGAIN.getMessage());
        }
        confirmationTokenDao.update(confirmationToken);
    }

    @Override
    public void sendMessageWithLinkToUserEmail(String email, String token) throws ServerException {
        if (email == null) {
            throw new RuntimeException(EmailConfirmationErrors.EMPTY_EMAIL.getMessage());
        }
        if (token == null) {
            throw new RuntimeException(EmailConfirmationErrors.CAN_NOT_CREATE_LINK_TRY_AGAIN.getMessage());
        }
        mailSender.sendMail(email, SUBJECT, composeEmail(token));
    }

    @Override
    @Transactional
    public void deleteByUserId(long userId) {
        confirmationTokenDao.deleteByUserId(userId);
    }

    private String composeEmail(String token) {
        return MAIL_BODY_BEGINNING +
                applicationLink +
                LINK_PATH +
                token +
                MAIL_BODY_ENDING;
    }

}
