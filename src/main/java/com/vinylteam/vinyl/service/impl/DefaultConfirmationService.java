package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.Starter;
import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.util.MailSender;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DefaultConfirmationService implements ConfirmationService {

    private final ConfirmationTokenDao confirmationTokenDao;
    private final MailSender mailSender;
    private static final String SUBJECT = "Confirm your email on wax-deals.com";
    private static final String MAIL_BODY_BEGINNING = "To confirm click on the link below and log into your account.\n";
    private static final String LINK_PATH = "/emailConfirmation?token=";
    private static final String MAIL_BODY_ENDING = "\n Thank you!\n\nWith kindest regards,\nWax-Deals team";

    public DefaultConfirmationService(ConfirmationTokenDao confirmationTokenDao, MailSender mailSender) {
        this.confirmationTokenDao = confirmationTokenDao;
        this.mailSender = mailSender;
    }

    @Override
    public Optional<ConfirmationToken> findByToken(UUID token) {
        return confirmationTokenDao.findByToken(token);
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
    public boolean addByUserId(long userId) {
        boolean isAdded;
        if (userId <= 0) {
            IllegalArgumentException e = new IllegalArgumentException();
            log.error("Id is 0 or less {'userId':{}}", userId, e);
            throw e;
        }
        ConfirmationToken newConfirmationToken = new ConfirmationToken();
        newConfirmationToken.setUserId(userId);
        newConfirmationToken.setToken(UUID.randomUUID());
        isAdded = confirmationTokenDao.add(newConfirmationToken);
        if (isAdded) {
            log.debug("Successfully added new confirmation token for user with userId {'userId':{}}", userId);
        } else {
            log.debug("Failed to add new confirmation token for user with userId {'userId':{}}", userId);
        }
        return isAdded;
    }

    @Override
    public boolean update(ConfirmationToken confirmationToken) {
        boolean isUpdated;
        if (confirmationToken == null) {
            NullPointerException e = new NullPointerException();
            log.error("Passed confirmation token is null", e);
            throw e;
        }
        isUpdated = confirmationTokenDao.update(confirmationToken);
        if (isUpdated) {
            log.debug("Successfully updated confirmation token {'confirmationToken':{}}", confirmationToken);
        } else {
            log.debug("Failed to update confirmation token {'confirmationToken':{}}", confirmationToken);
        }
        return isUpdated;
    }

    @Override
    public boolean sendMessageWithLinkToUserEmail(User user) {
        if (user == null) {
            log.error("User is null, not adding token and sending email with confirmation link.");
            throw new RuntimeException("User is null");
        }
        if (user.getEmail() == null) {
            log.error("User's email is null, not adding token and sending email with confirmation link.");
            throw new RuntimeException("User's email is null");
        }
        boolean isSent;
        long userId = user.getId();
        Optional<ConfirmationToken> userConfirmationToken = confirmationTokenDao.findByUserId(userId);
        if (userConfirmationToken.isEmpty()) {
            if (!addByUserId(userId)) {
                IllegalStateException e = new IllegalStateException();
                log.error("Couldn't add confirmation token for user after confirming there isn't any in the db {'user':{}}", user, e);
                throw e;
            }
        } else {
            userConfirmationToken.get().setToken(UUID.randomUUID());
            update(userConfirmationToken.get());
        }
        userConfirmationToken = confirmationTokenDao.findByUserId(userId);
        isSent = mailSender.sendMail(user.getEmail(), SUBJECT, composeEmail(user.getEmail(), userConfirmationToken.get().getToken()));
        if (isSent) {
            log.info("Confirmation email is sent to user {'user':{}}", user);
        } else {
            log.info("Failed to send confirmation email to user {'user':{}}", user);
        }
        return isSent;
    }

    @Override
    public boolean deleteByUserId(long userId) {
        if (userId <= 0) {
            IllegalArgumentException e = new IllegalArgumentException();
            log.error("Id is 0 or less {'userId':{}}", userId, e);
            throw e;
        }
        boolean isDeleted = confirmationTokenDao.deleteByUserId(userId);
        if (isDeleted) {
            log.debug("Confirmation token for user with userId was deleted {'userId':{}}", userId);
        } else {
            log.debug("Confirmation token for user with userId wasn't deleted {'userId':{}}", userId);
        }
        return isDeleted;
    }

    private String composeEmail(String email, UUID token) {
        StringBuilder confirmationEmailBuilder = new StringBuilder();
        confirmationEmailBuilder.append(MAIL_BODY_BEGINNING);
        if (System.getenv("env") == null) {
            confirmationEmailBuilder.append(Starter.PROPERTIES_READER.getProperty("localLink"));
            confirmationEmailBuilder.append(Starter.PROPERTIES_READER.getProperty("appPort"));
        } else {
            confirmationEmailBuilder.append(Starter.PROPERTIES_READER.getProperty("prodLink"));
        }
        confirmationEmailBuilder.append(LINK_PATH);
        confirmationEmailBuilder.append(token.toString());
        confirmationEmailBuilder.append(MAIL_BODY_ENDING);
        return confirmationEmailBuilder.toString();
    }

}
