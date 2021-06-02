package com.vinylteam.vinyl.service.impl;

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
    private static final String MAIL_BODY_BEGINNING_WITH_LINK = "To confirm click on the link below and log into your account. " +
            "\n https://wax-deals.herokuapp.com/emailConfirmation?token=";
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
        Optional<ConfirmationToken> optionalToken;
        if (userId > 0) {
            optionalToken = confirmationTokenDao.findByUserId(userId);
        } else {
            IllegalArgumentException e = new IllegalArgumentException();
            log.error("Id is 0 or less {'userId':{}}", userId, e);
            throw new RuntimeException(e);
        }
        log.debug("Resulting optional with confirmation token is {'optionalConfirmationToken':{}}", optionalToken);
        return optionalToken;
    }

    @Override
    public boolean addByUserId(long userId) {
        boolean isAdded;
        if (userId > 0) {
            ConfirmationToken newConfirmationToken = new ConfirmationToken();
            newConfirmationToken.setUserId(userId);
            newConfirmationToken.setToken(UUID.randomUUID());
            isAdded = confirmationTokenDao.add(newConfirmationToken);
        } else {
            IllegalArgumentException e = new IllegalArgumentException();
            log.error("Id is 0 or less {'userId':{}}", userId, e);
            throw new RuntimeException(e);
        }
        if (isAdded) {
            log.debug("Successfully added new confirmation token for user with userId {'userId':{}}", userId);
        } else {
            log.debug("Failed to add new confirmation token for user with userId {'userId':{}}", userId);
        }
        return isAdded;
    }

    @Override
    public boolean sendMessageWithLinkToUserEmail(User user) {
        boolean isSent;
        if (user != null) {
            if (user.getEmail() != null) {
                long userId = user.getId();
                Optional<ConfirmationToken> userConfirmationToken = confirmationTokenDao.findByUserId(userId);
                if (userConfirmationToken.isEmpty()) {
                    if (addByUserId(userId)) {
                        userConfirmationToken = confirmationTokenDao.findByUserId(userId);
                    } else {
                        IllegalStateException e = new IllegalStateException();
                        log.error("Couldn't add confirmation token for user after confirming there isn't any in the db {'user':{}}", user, e);
                        throw new RuntimeException(e);
                    }
                }
                StringBuilder confirmationEmailBuilder = new StringBuilder();
                confirmationEmailBuilder.append(MAIL_BODY_BEGINNING_WITH_LINK);
                confirmationEmailBuilder.append(userConfirmationToken.get().getToken().toString());
                confirmationEmailBuilder.append(MAIL_BODY_ENDING);
                String confirmationEmail = confirmationEmailBuilder.toString();
                isSent = mailSender.sendMail(user.getEmail(), SUBJECT, confirmationEmail);
            } else {
                IllegalArgumentException e = new IllegalArgumentException("User's email is null");
                log.error("User's email is null, not adding token and sending email with confirmation link.");
                throw new RuntimeException(e);
            }
        } else {
            IllegalArgumentException e = new IllegalArgumentException("User is null");
            log.error("User is null, not adding token and sending email with confirmation link.");
            throw new RuntimeException(e);
        }
        if (isSent) {
            log.info("Confirmation email is sent to user {'user':{}}", user);
        } else {
            log.info("Failed to send confirmation email to user {'user':{}}", user);
        }
        return isSent;
    }

    @Override
    public boolean deleteByUserId(long userId) {
        boolean isDeleted;
        if (userId > 0) {
            isDeleted = confirmationTokenDao.deleteByUserId(userId);
        } else {
            IllegalArgumentException e = new IllegalArgumentException();
            log.error("Id is 0 or less {'userId':{}}", userId, e);
            throw new RuntimeException(e);
        }
        if (isDeleted) {
            log.debug("Confirmation token for user with userId was deleted {'userId':{}}", userId);
        } else {
            log.debug("Confirmation token for user with userId wasn't deleted {'userId':{}}", userId);
        }
        return isDeleted;
    }

}
