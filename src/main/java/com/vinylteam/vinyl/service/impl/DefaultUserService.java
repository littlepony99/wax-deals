package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.UserServiceException;
import com.vinylteam.vinyl.exception.entity.ErrorUser;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ConfirmationService confirmationService;

    @Override
    @Transactional
    public void register(UserInfoRequest userProfileInfo) {
        String email = userProfileInfo.getEmail();
        String password = userProfileInfo.getPassword();
        if (!isNotEmptyNotNull(email)) {
            throw new UserServiceException(ErrorUser.EMPTY_EMAIL.getMessage());
        }
        if (!isNotEmptyNotNull(password)) {
            throw new UserServiceException(ErrorUser.EMPTY_PASSWORD.getMessage());
        }
        securityService.emailFormatCheck(email);
        securityService.validatePassword(password, userProfileInfo.getPasswordConfirmation());
        User userToAdd = securityService
                .createUserWithHashedPassword(email, password.toCharArray());
        userToAdd.setDiscogsUserName(userProfileInfo.getDiscogsUserName());
        long userId;
        try {
            userId = userDao.add(userToAdd);
        } catch (DuplicateKeyException e) {
            throw new UserServiceException(ErrorUser.ADD_USER_EXISTING_EMAIL.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new UserServiceException(ErrorUser.ADD_USER_INVALID_VALUES.getMessage());
        }
        log.debug("Added created user to db {'user':{}}", userToAdd);
        ConfirmationToken confirmationToken = confirmationService.addByUserId(userId);
        confirmationService.sendMessageWithLinkToUserEmail(userToAdd.getEmail(), confirmationToken.getToken().toString());
    }

    @Transactional
    @Override
    public void confirmEmail(UserInfoRequest userInfo) {
        signInCheck(userInfo);
        User user = findByEmail(userInfo.getEmail()).get();
        confirmationService.deleteByUserId(user.getId());
        userDao.setUserStatusTrue(user.getId());
    }

    @Override
    public void delete(User user) {
        userDao.delete(user);
    }

    @Override
    public Optional<User> findById(long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new UserServiceException(ErrorUser.EMAIL_NOT_FOUND_IN_DB.getMessage()));
        return Optional.of(user);
    }

    @Override
    public void update(String oldEmail, String newEmail, String newPassword, String newDiscogsUserName) {
        if (!isNotEmptyNotNull(oldEmail) || !isNotEmptyNotNull(newEmail)) {
            throw new UserServiceException(ErrorUser.EMPTY_EMAIL.getMessage());
        }
        securityService.emailFormatCheck(newEmail);
        if (!isNotEmptyNotNull(newPassword)) {
            throw new UserServiceException(ErrorUser.EMPTY_PASSWORD.getMessage());
        }
        securityService.passwordFormatCheck(newPassword);
        User changedUser = securityService.createUserWithHashedPassword(newEmail, newPassword.toCharArray());
        if (oldEmail.equalsIgnoreCase(newEmail)) {
            changedUser.setStatus(true);
        }
        changedUser.setDiscogsUserName(newDiscogsUserName);
        try {
            userDao.update(oldEmail, changedUser);
        } catch (DuplicateKeyException e) {
            throw new UserServiceException(ErrorUser.UPDATE_USER_EXISTING_EMAIL.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new UserServiceException(ErrorUser.UPDATE_USER_ERROR.getMessage());
        }
        if (!changedUser.getStatus()) {
            User user = findByEmail(newEmail).get();
            ConfirmationToken token = confirmationService.addByUserId(user.getId());
            confirmationService.sendMessageWithLinkToUserEmail(newEmail, token.getToken().toString());
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (!isNotEmptyNotNull(email)) {
            throw new UserServiceException(ErrorUser.EMPTY_EMAIL.getMessage());
        }
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new UserServiceException(ErrorUser.EMAIL_NOT_FOUND_IN_DB.getMessage()));
        return Optional.of(user);
    }

    @Override
    public void signInCheck(UserInfoRequest userProfileInfo) {
        if (!isNotEmptyNotNull(userProfileInfo.getEmail())) {
            throw new UserServiceException(ErrorUser.EMPTY_EMAIL.getMessage());
        }
        if (!isNotEmptyNotNull(userProfileInfo.getPassword())) {
            throw new UserServiceException(ErrorUser.EMPTY_PASSWORD.getMessage());
        }
        User userToCheckAgainst;
        try {
            userToCheckAgainst = findByEmail(userProfileInfo.getEmail()).get();
        } catch (UserServiceException e) {
            throw new UserServiceException(ErrorUser.WRONG_CREDENTIALS.getMessage());
        }
        if (!securityService.checkPasswordAgainstUserPassword(userToCheckAgainst, userProfileInfo.getPassword().toCharArray())) {
            throw new UserServiceException(ErrorUser.WRONG_CREDENTIALS.getMessage());
        }
        if (!userToCheckAgainst.getStatus()) {
            throw new UserServiceException(ErrorUser.EMAIL_NOT_VERIFIED.getMessage());
        }
    }

    @Override
    public Optional<User> editProfile(UserInfoRequest userProfileInfo,
                                      User user) {
        String oldEmail = user.getEmail();
        String oldPassword = userProfileInfo.getPassword();
        if (!isNotEmptyNotNull(oldPassword)) {
            throw new UserServiceException(ErrorUser.EMPTY_PASSWORD.getMessage());
        }
        boolean checkOldPassword = securityService.checkPasswordAgainstUserPassword(user, oldPassword.toCharArray());
        if (!checkOldPassword) {
            throw new UserServiceException(ErrorUser.WRONG_PASSWORD.getMessage());
        }
        if (!isNotEmptyNotNull(oldEmail)) {
            throw new UserServiceException(ErrorUser.EMPTY_EMAIL.getMessage());
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
        if (string != null) {
            if (!string.isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
