package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.service.ConfirmationService;
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
public class DefaultConfirmationService implements ConfirmationService {

    private final ConfirmationTokenDao confirmationTokenDao;
    private final UserDao userDao;
    private final MailSender mailSender;
    @Value("${application.link}")
    private String applicationLink;

    private static final String SUBJECT = "Confirm your email on wax-deals.com";
    private static final String MAIL_BODY_BEGINNING = "To confirm click on the link below and log into your account.\n";
    private static final String LINK_PATH = "/emailConfirmation?token=";
    private static final String MAIL_BODY_ENDING = "\n Thank you!\n\nWith kindest regards,\nWax-Deals team";

    @Override
    public Optional<ConfirmationToken> findByToken(String token) {
        try {
            UUID tokenUUID = UUID.fromString(token);
            return confirmationTokenDao.findByToken(tokenUUID);
        } catch (IllegalArgumentException e) {
            log.warn("Token is incorrect UUID", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ConfirmationToken> findByUserId(long userId) {
        if (userId <= 0) {
            IllegalArgumentException e = new IllegalArgumentException();
            log.error("Id is 0 or less {'userId':{}}", userId, e);
            throw e;
        }
        Optional<ConfirmationToken> optionalToken = confirmationTokenDao.findByUserId(userId);
        log.debug("Resulting optional with confirmation token is {'optionalConfirmationToken':{}}", optionalToken);
        return optionalToken;
    }

    @Override
    public ConfirmationToken addByUserId(long userId) {
        if (userId <= 0) {
            log.error("Id is 0 or less {'userId':{}}", userId);
            throw new IllegalArgumentException();
        }
        ConfirmationToken newConfirmationToken = ConfirmationToken.builder()
                .userId(userId)
                .token(UUID.randomUUID())
                .build();
        confirmationTokenDao.add(newConfirmationToken);
        return newConfirmationToken;
    }

    @Override
    public void update(ConfirmationToken confirmationToken) {
        if (confirmationToken == null) {
            log.error("Passed confirmation token is null");
            throw new RuntimeException("Passed confirmation token is null");
        }
        confirmationTokenDao.update(confirmationToken);
    }

    @Override
    public void sendMessageWithLinkToUserEmail(String email, String token) {
        if (email == null) {
            log.error("User's email is null, not adding token and sending email with confirmation link.");
            throw new RuntimeException("User's email is null");
        }
        mailSender.sendMail(email, SUBJECT, composeEmail(token));
    }

    @Override
    @Transactional
    public void deleteByUserId(long userId) {
        if (userId <= 0) {
            log.error("Id is 0 or less {'userId':{}}", userId);
            throw new IllegalArgumentException("Not correct user id=" + userId);
        }
        confirmationTokenDao.deleteByUserId(userId);
        userDao.setUserStatusTrue(userId);
    }

    private String composeEmail(String token) {
        return MAIL_BODY_BEGINNING +
                applicationLink +
                LINK_PATH +
                token +
                MAIL_BODY_ENDING;
    }

}
