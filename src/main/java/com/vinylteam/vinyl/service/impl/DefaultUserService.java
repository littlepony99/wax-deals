package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.entity.UserErrors;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.EmailConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private final UserDao userDao;
    private final SecurityService securityService;
    private final EmailConfirmationService emailConfirmationService;

    @Override
    @Transactional
    public void register(UserInfoRequest userInfoRequest) {
        String email = userInfoRequest.getEmail();
        String password = userInfoRequest.getPassword();
        if (!isNotEmptyNotNull(email)) {
            throw new RuntimeException(UserErrors.EMPTY_EMAIL_ERROR.getMessage());
        }
        if (!isNotEmptyNotNull(password)) {
            throw new RuntimeException(UserErrors.EMPTY_PASSWORD_ERROR.getMessage());
        }
        //securityService.emailFormatCheck(email);
        securityService.validatePassword(password, userInfoRequest.getPasswordConfirmation());
        User userToAdd = securityService
                .createUserWithHashedPassword(email, password.toCharArray());
        userToAdd.setDiscogsUserName(userInfoRequest.getDiscogsUserName());
        long userId;
        try {
            userId = userDao.add(userToAdd);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException(UserErrors.ADD_USER_EXISTING_EMAIL_ERROR.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(UserErrors.ADD_USER_INVALID_VALUES_ERROR.getMessage());
        }
        log.debug("Added created user to db {'user':{}}", userToAdd);
        //ConfirmationToken confirmationToken = emailConfirmationService.addByUserId(userId);
        //emailConfirmationService.sendMessageWithLinkToUserEmail(userToAdd.getEmail(), confirmationToken.getToken().toString());
    }

    @Transactional
    @Override
    public User confirmEmail(UserInfoRequest userInfo) {
        signInCheck(userInfo);
        User user = findByEmail(userInfo.getEmail());
        emailConfirmationService.deleteByUserId(user.getId());
        userDao.setUserStatusTrue(user.getId());
        return findByEmail(userInfo.getEmail());
    }

    @Override
    public void delete(User user) {
        userDao.delete(user);
    }

    @Override
    public Optional<User> findById(long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new RuntimeException(UserErrors.EMAIL_NOT_FOUND_IN_DB_ERROR.getMessage()));
        return Optional.of(user);
    }

    @Override
    public void update(String oldEmail, String newEmail, String newPassword, String newDiscogsUserName) {
        if (!isNotEmptyNotNull(oldEmail) || !isNotEmptyNotNull(newEmail)) {
            throw new RuntimeException(UserErrors.EMPTY_EMAIL_ERROR.getMessage());
        }
        securityService.emailFormatCheck(newEmail);
        if (!isNotEmptyNotNull(newPassword)) {
            throw new RuntimeException(UserErrors.EMPTY_PASSWORD_ERROR.getMessage());
        }
        securityService.validatePassword(newPassword);
        User changedUser = securityService.createUserWithHashedPassword(newEmail, newPassword.toCharArray());
        if (oldEmail.equalsIgnoreCase(newEmail)) {
            changedUser.setStatus(true);
        }
        changedUser.setDiscogsUserName(newDiscogsUserName);
        try {
            userDao.update(oldEmail, changedUser);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException(UserErrors.UPDATE_USER_EXISTING_EMAIL_ERROR.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(UserErrors.UPDATE_USER_ERROR.getMessage());
        }
        if (!changedUser.getStatus()) {
            User user = findByEmail(newEmail);
            ConfirmationToken token = emailConfirmationService.addByUserId(user.getId());
            emailConfirmationService.sendMessageWithLinkToUserEmail(newEmail, token.getToken().toString());
        }
    }

    @Override
    public User findByEmail(String email) {
        if (!isNotEmptyNotNull(email)) {
            throw new RuntimeException(UserErrors.EMPTY_EMAIL_ERROR.getMessage());
        }
        return userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(UserErrors.EMAIL_NOT_FOUND_IN_DB_ERROR.getMessage()));
    }

    @Override
    public void signInCheck(UserInfoRequest userProfileInfo) {
        if (!isNotEmptyNotNull(userProfileInfo.getEmail())) {
            throw new RuntimeException(UserErrors.EMPTY_EMAIL_ERROR.getMessage());
        }
        if (!isNotEmptyNotNull(userProfileInfo.getPassword())) {
            throw new RuntimeException(UserErrors.EMPTY_PASSWORD_ERROR.getMessage());
        }
        User userToCheckAgainst;
        try {
            userToCheckAgainst = findByEmail(userProfileInfo.getEmail());
        } catch (RuntimeException e) {
            throw new RuntimeException(UserErrors.WRONG_CREDENTIALS_ERROR.getMessage());
        }
        if (!securityService.validateIfPasswordMatches(userToCheckAgainst, userProfileInfo.getPassword().toCharArray())) {
            throw new RuntimeException(UserErrors.WRONG_CREDENTIALS_ERROR.getMessage());
        }
        if (!userToCheckAgainst.getStatus()) {
            throw new RuntimeException(UserErrors.EMAIL_NOT_VERIFIED_ERROR.getMessage());
        }
    }

    @Override
    public User editProfile(UserInfoRequest userProfileInfo,
                            User user) {
        String oldEmail = user.getEmail();
        String oldPassword = userProfileInfo.getPassword();
        if (!isNotEmptyNotNull(oldPassword)) {
            throw new RuntimeException(UserErrors.EMPTY_PASSWORD_ERROR.getMessage());
        }
        boolean checkOldPassword = securityService.validateIfPasswordMatches(user, oldPassword.toCharArray());
        if (!checkOldPassword) {
            throw new RuntimeException(UserErrors.WRONG_PASSWORD_ERROR.getMessage());
        }
        if (!isNotEmptyNotNull(oldEmail)) {
            throw new RuntimeException(UserErrors.EMPTY_EMAIL_ERROR.getMessage());
        }
        String newPassword = userProfileInfo.getNewPassword();
        if (isNotEmptyNotNull(newPassword)) {
            String newPasswordConfirmation = userProfileInfo.getNewPasswordConfirmation();
            securityService.validatePassword(newPassword, newPasswordConfirmation);
        } else {
            newPassword = oldPassword;
        }
        String newEmail = userProfileInfo.getEmail();
        if (isNotEmptyNotNull(newEmail)) {
            securityService.emailFormatCheck(newEmail);
        } else {
            newEmail = oldEmail;
        }
        String newDiscogsUserName = userProfileInfo.getDiscogsUserName();
        if (!isNotEmptyNotNull(newDiscogsUserName)) {
            newDiscogsUserName = user.getDiscogsUserName();
        }
        update(oldEmail, newEmail, newPassword, newDiscogsUserName);
        return findByEmail(newEmail);
    }

    boolean isNotEmptyNotNull(String string) {
        return !StringUtils.isBlank(string);
    }

}
