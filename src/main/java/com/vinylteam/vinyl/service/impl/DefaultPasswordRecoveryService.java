package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.PasswordRecoveryDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.RecoveryPasswordException;
import com.vinylteam.vinyl.exception.entity.ErrorRecoveryPassword;
import com.vinylteam.vinyl.service.PasswordRecoveryService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Configuration
@PropertySource("classpath:application.properties")
public class DefaultPasswordRecoveryService implements PasswordRecoveryService {
    private static final String RECOVERY_MESSAGE = "Hello, to change your password, follow this link: \n{applicationLink}/recoveryPassword/newPassword?token=";

    private final PasswordRecoveryDao passwordRecoveryDao;
    private final UserService userService;
    private final MailSender mailSender;
    @Value("${application.link}")
    private String applicationLink;
    @Value("${recoveryToken.live.hours}")
    private int liveTokenHours;

    @Override
    public void changePassword(UserChangeProfileInfoRequest userProfileInfo) {
        String newPassword = userProfileInfo.getNewPassword();
        checkPassword(newPassword, userProfileInfo.getConfirmNewPassword());
        UUID tokenUUID = stringToUUD(userProfileInfo.getToken());
        RecoveryToken recoveryToken = passwordRecoveryDao.findByToken(tokenUUID)
                .orElseThrow(() -> new RecoveryPasswordException(ErrorRecoveryPassword.TOKEN_NOT_FOUND_IN_DB.getMessage()));
        User user = userService.findById(recoveryToken.getUserId())
                .orElseThrow(() -> new RecoveryPasswordException(ErrorRecoveryPassword.TOKEN_NOT_FOUND_IN_DB.getMessage()));
        String email = user.getEmail();
        userService.update(email, email, newPassword, user.getDiscogsUserName());
        passwordRecoveryDao.deleteById(recoveryToken.getId());
    }

    @Override
    public void checkToken(String token) {
        UUID tokenUUID = stringToUUD(token);
        RecoveryToken recoveryToken = passwordRecoveryDao.findByToken(tokenUUID)
                .orElseThrow(() -> new RecoveryPasswordException(ErrorRecoveryPassword.TOKEN_NOT_FOUND_IN_DB.getMessage()));
        LocalDateTime tokenLifetime = recoveryToken.getCreatedAt().toLocalDateTime().plusHours(liveTokenHours);
        if (tokenLifetime.compareTo(LocalDateTime.now()) < 0) {
            log.debug("Token lifetime has come to an end.");
            passwordRecoveryDao.deleteById(recoveryToken.getId());
            throw new RecoveryPasswordException(ErrorRecoveryPassword.TOKEN_IS_EXPIRED.getMessage());
        }
    }

    @Override
    @Transactional
    public void sendLink(String email) {
        checkForNotNull(email, ErrorRecoveryPassword.EMPTY_EMAIL);
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RecoveryPasswordException(ErrorRecoveryPassword.EMAIL_NOT_FOUND_IN_DB.getMessage()));
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
        mailSender.sendMail(email, recoveryTopic, recoveryLink);
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
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setUserId(userId);
        recoveryToken.setToken(token);
        passwordRecoveryDao.add(recoveryToken);
        return recoveryToken;
    }

}
