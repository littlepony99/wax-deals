package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.PasswordRecoveryDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.PasswordRecoveryException;
import com.vinylteam.vinyl.exception.entity.ErrorPasswordRecovery;
import com.vinylteam.vinyl.service.PasswordRecoveryService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultPasswordRecoveryService implements PasswordRecoveryService {
    private static final String RECOVERY_MESSAGE = "Hello, to change your password, follow this link: \n{applicationLink}/recoveryPassword/newPassword?token=";
    @Value("${application.link}")
    private String applicationLink;
    @Value("${recoveryToken.live.hours}")
    private int liveTokenHours;
    private final PasswordRecoveryDao passwordRecoveryDao;
    private final UserService userService;
    private final MailSender mailSender;


    @Transactional
    @Override
    public void changePassword(UserInfoRequest userProfileInfo) {
        String newPassword = userProfileInfo.getNewPassword();
        checkPassword(newPassword, userProfileInfo.getNewPasswordConfirmation());
        UUID tokenUUID = stringToUUID(userProfileInfo.getToken());
        RecoveryToken recoveryToken = passwordRecoveryDao.findByToken(tokenUUID)
                .orElseThrow(() -> new PasswordRecoveryException(ErrorPasswordRecovery.TOKEN_NOT_FOUND_IN_DB.getMessage()));
        User user = userService.findById(recoveryToken.getUserId())
                .orElseThrow(() -> new PasswordRecoveryException(ErrorPasswordRecovery.TOKEN_NOT_FOUND_IN_DB.getMessage()));
        String email = user.getEmail();
        try {
            userService.update(email, email, newPassword, user.getDiscogsUserName());
            passwordRecoveryDao.deleteById(recoveryToken.getId());
        } catch (DataAccessException e) {
            throw new PasswordRecoveryException(ErrorPasswordRecovery.UPDATE_PASSWORD_ERROR.getMessage());
        }
    }

    @Override
    public void checkToken(String token) {
        UUID tokenUUID = stringToUUID(token);
        RecoveryToken recoveryToken = passwordRecoveryDao.findByToken(tokenUUID)
                .orElseThrow(() -> new PasswordRecoveryException(ErrorPasswordRecovery.TOKEN_NOT_FOUND_IN_DB.getMessage()));
        LocalDateTime tokenLifetime = recoveryToken.getCreatedAt().toLocalDateTime().plusHours(liveTokenHours);
        if (tokenLifetime.compareTo(LocalDateTime.now()) < 0) {
            log.debug("Token lifetime has come to an end.");
            try {
                passwordRecoveryDao.deleteById(recoveryToken.getId());
            } catch (DataAccessException e) {
                throw new PasswordRecoveryException(ErrorPasswordRecovery.DELETE_TOKEN_ERROR.getMessage());
            }
            throw new PasswordRecoveryException(ErrorPasswordRecovery.TOKEN_IS_EXPIRED.getMessage());
        }
    }

    @Override
    @Transactional
    public void sendLink(String email) {
        checkForIsNotEmptyNotNull(email, ErrorPasswordRecovery.EMPTY_EMAIL);
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new PasswordRecoveryException(ErrorPasswordRecovery.EMAIL_NOT_FOUND_IN_DB.getMessage()));
        RecoveryToken recoveryToken = addRecoveryTokenWithUserId(user.getId());
        sendEmailWithLink(email, recoveryToken.getToken().toString());
    }

    UUID stringToUUID(String token) {
        try {
            return UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new PasswordRecoveryException(ErrorPasswordRecovery.TOKEN_NOT_CORRECT_UUID.getMessage());
        }
    }

    void sendEmailWithLink(String email, String recoveryToken) {
        String recoveryLink = RECOVERY_MESSAGE.replace("{applicationLink}", applicationLink) + recoveryToken;
        String recoveryTopic = "Password Recovery - WaxDeals";
        try {
            mailSender.sendMail(email, recoveryTopic, recoveryLink);
        } catch (RuntimeException e) {
            throw new PasswordRecoveryException(ErrorPasswordRecovery.EMAIL_SEND_ERROR.getMessage());
        }
    }

    void checkPassword(String newPassword, String confirmPassword) {
        checkForIsNotEmptyNotNull(newPassword, ErrorPasswordRecovery.EMPTY_PASSWORD);
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordRecoveryException(ErrorPasswordRecovery.PASSWORDS_NOT_EQUAL.getMessage());
        }
    }

    void checkForIsNotEmptyNotNull(String value, ErrorPasswordRecovery emptyValue) {
        if (value == null || value.isEmpty()) {
            throw new PasswordRecoveryException(emptyValue.getMessage());
        }
    }

    RecoveryToken addRecoveryTokenWithUserId(long userId) {
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setUserId(userId);
        recoveryToken.setToken(token);
        try {
            passwordRecoveryDao.add(recoveryToken);
        } catch (DuplicateKeyException e) {
            throw new PasswordRecoveryException((ErrorPasswordRecovery.ADD_TOKEN_ERROR.getMessage()));
        } catch (DataIntegrityViolationException e) {
            throw new PasswordRecoveryException(ErrorPasswordRecovery.ADD_TOKEN_ERROR.getMessage());
        }
        return recoveryToken;
    }

}
