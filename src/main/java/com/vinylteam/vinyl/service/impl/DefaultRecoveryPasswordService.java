package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.RecoveryPasswordDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.RecoveryPasswordException;
import com.vinylteam.vinyl.exception.entity.ErrorRecoveryPassword;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.MailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DefaultRecoveryPasswordService implements RecoveryPasswordService {
    private static final String RECOVERY_MESSAGE = "Hello, to change your password, follow this link: \n{applicationLink}/newPassword?token=";

    private final RecoveryPasswordDao recoveryPasswordDao;
    private final UserService userService;
    private final MailSender mailSender;
    private final String applicationLink;
    private final int liveTokenHours;

    @Override
    //@Transactional
    public void changePassword(String newPassword, String confirmPassword, String token) {
        checkPassword(newPassword, confirmPassword);
        UUID tokenUUID = stringToUUD(token);
        RecoveryToken recoveryToken = recoveryPasswordDao.findByToken(tokenUUID)
                .orElseThrow(() -> {
                    throw new RecoveryPasswordException(ErrorRecoveryPassword.TOKEN_NOT_FOUND_IN_DB.getMessage());
                });
        User user = userService.findById(recoveryToken.getUserId())
                .orElseThrow(() -> new RecoveryPasswordException(ErrorRecoveryPassword.TOKEN_NOT_FOUND_IN_DB.getMessage()));
        String email = user.getEmail();
        if (userService.update(email, email, newPassword, user.getDiscogsUserName())) {
            recoveryPasswordDao.deleteById(recoveryToken.getId());
        } else {
            throw new RecoveryPasswordException(ErrorRecoveryPassword.UPDATE_PASSWORD_ERROR.getMessage());
        }
    }

    @Override
    public void checkToken(String token) {
        UUID tokenUUID = stringToUUD(token);
        RecoveryToken recoveryToken = recoveryPasswordDao.findByToken(tokenUUID)
                .orElseThrow(() -> {
                    throw new RecoveryPasswordException(ErrorRecoveryPassword.TOKEN_NOT_FOUND_IN_DB.getMessage());
                });
        LocalDateTime tokenLifetime = recoveryToken.getCreatedAt().toLocalDateTime().plusHours(liveTokenHours);
        if (tokenLifetime.compareTo(LocalDateTime.now()) < 0) {
            log.debug("Token lifetime has come to an end.");
            recoveryPasswordDao.deleteById(recoveryToken.getId());
            throw new RecoveryPasswordException(ErrorRecoveryPassword.TOKEN_IS_EXPIRED.getMessage());
        }
    }

    @Override
    public void sendLink(String email) {
        checkForNotNull(email, ErrorRecoveryPassword.EMPTY_EMAIL);
        User user = userService.findByEmail(email).orElseThrow(
                () -> {
                    throw new RecoveryPasswordException(ErrorRecoveryPassword.EMAIL_NOT_FOUND_IN_DB.getMessage());
                }
        );
        RecoveryToken recoveryToken = addRecoveryUserToken(user.getId());
        sendEmailWithLink(email, recoveryToken.getToken().toString());
    }

    UUID stringToUUD(String token) {
        try {
            return UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            throw new RecoveryPasswordException(ErrorRecoveryPassword.TOKEN_NOT_CORRECT_UUID.getMessage());
        }
    }

    void sendEmailWithLink(String email, String recoveryToken) {
        String recoveryLink = RECOVERY_MESSAGE.replace("{applicationLink}", applicationLink) + recoveryToken;
        String recoveryTopic = "Recovery Password - WaxDeals";
        boolean isSent = mailSender.sendMail(email, recoveryTopic, recoveryLink);
        if (!isSent) {
            throw new RecoveryPasswordException(ErrorRecoveryPassword.EMAIL_SEND_ERROR.getMessage());
        }
    }

    void checkPassword(String newPassword, String confirmPassword) {
        checkForNotNull(newPassword, ErrorRecoveryPassword.EMPTY_PASSWORD);
        if (!newPassword.equals(confirmPassword)) {
            throw new RecoveryPasswordException(ErrorRecoveryPassword.PASSWORDS_NOT_EQUAL.getMessage());
        }
    }

    void checkForNotNull(String value, ErrorRecoveryPassword emptyValue) {
        if (value == null || value.isEmpty()) {
            throw new RecoveryPasswordException(emptyValue.getMessage());
        }
    }

    RecoveryToken addRecoveryUserToken(long userId) {
        boolean isAdded;
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setUserId(userId);
        recoveryToken.setToken(token);
        isAdded = recoveryPasswordDao.add(recoveryToken);
        if (!isAdded) {
            throw new RecoveryPasswordException(ErrorRecoveryPassword.ADD_TOKEN_ERROR.getMessage());
        }
        return recoveryToken;
    }
}
