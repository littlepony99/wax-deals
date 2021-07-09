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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
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
        if (newEmail != null && newPassword != null && oldEmail != null) {
            User changedUser = securityService.createUserWithHashedPassword(newEmail, newPassword.toCharArray());
            if (oldEmail.equalsIgnoreCase(newEmail)) {
                changedUser.setStatus(true);
            }
            changedUser.setDiscogsUserName(newDiscogsUserName);
            userDao.update(oldEmail, changedUser);
        } else {
            log.error("At least one of passed to DefaultUserService.update(...) arguments is null {'oldEmail': {}, 'newEmail': {}, {}'newDiscogsUserName': {}}",
                    oldEmail == null ? "null" : oldEmail, newEmail == null ? "null" : newEmail,
                    newPassword == null ? "'newPassword': null, " : "", newDiscogsUserName == null ? "null" : newDiscogsUserName);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new UserServiceException(ErrorUser.EMAIL_NOT_FOUND_IN_DB.getMessage()));
        return Optional.of(user);
    }

    @Override
    public void signInCheck(UserInfoRequest userProfileInfo) {
        User userToCheckAgainst = findByEmail(userProfileInfo.getEmail()).orElseThrow(
                () -> new UserServiceException(ErrorUser.WRONG_CREDENTIALS.getMessage())
        );
        if (!securityService.checkPasswordAgainstUserPassword(userToCheckAgainst, userProfileInfo.getPassword().toCharArray())) {
            throw new UserServiceException(ErrorUser.WRONG_CREDENTIALS.getMessage());
        }
        if (!userToCheckAgainst.getStatus()) {
            throw new UserServiceException(ErrorUser.EMAIL_NOT_VERIFIED.getMessage());
        }
    }

    @Override
    public Optional<User> signInCheckConfirmation(UserInfoRequest userProfileInfo) {
        Optional<ConfirmationToken> optionalConfirmationTokenByLinkToken = confirmationService.findByToken(userProfileInfo.getToken());
        ConfirmationToken confirmationToken = optionalConfirmationTokenByLinkToken.orElseThrow(
                () -> new IllegalArgumentException("Sorry, something went wrong on our side, we're looking into it. Please, try to login again, or contact us."));
        User userByLinkToken = userDao.findById(confirmationToken.getUserId()).get();
        if (userByLinkToken.getEmail().equalsIgnoreCase(userByLinkToken.getEmail())) {
            if (securityService.checkPasswordAgainstUserPassword(
                    userByLinkToken, userProfileInfo.getPassword().toCharArray())) {
                confirmationService.deleteByUserId(userByLinkToken.getId());
                return Optional.of(userByLinkToken);
            }
        } else {
            log.error("Email is not correct, token was sent to {}", userByLinkToken.getEmail());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> editProfile(UserInfoRequest userProfileInfo,
                                      User user) {
        String oldPassword = userProfileInfo.getPassword();
        boolean checkOldPassword = securityService.checkPasswordAgainstUserPassword(user, oldPassword.toCharArray());
        if (!checkOldPassword) {
            throw new UserServiceException(ErrorUser.WRONG_PASSWORD.getMessage());
        }
        String newPassword = userProfileInfo.getNewPassword();
        if (isNotEmptyNotNull(newPassword)) {
            String newPasswordConfirmation = userProfileInfo.getNewPasswordConfirmation();
            securityService.validatePassword(newPassword, newPasswordConfirmation);
        } else {
            newPassword = oldPassword;
        }
        String newEmail = userProfileInfo.getEmail();
        if (isNotEmptyNotNull(userProfileInfo.getEmail())) {
            securityService.emailFormatCheck(userProfileInfo.getEmail());
        } else {
            newEmail = user.getEmail();
        }
        User updatedUser = securityService.createUserWithHashedPassword(newEmail, newPassword.toCharArray());
        String newDiscogsUserName = userProfileInfo.getDiscogsUserName();
        if (!isNotEmptyNotNull(newDiscogsUserName)) {
            newDiscogsUserName = user.getDiscogsUserName();
        }
        updatedUser.setDiscogsUserName(newDiscogsUserName);
        try {
            userDao.update(user.getEmail(), updatedUser);
        } catch (EmptyResultDataAccessException e) {
            log.error("Failed to update user with new email, password, or discogs username in the database {'newEmail':{}, 'newDiscogsUserName':{}}.", newEmail, newDiscogsUserName);
            throw new UserServiceException(ErrorUser.UPDATE_USER_ERROR.getMessage());
        }
        return Optional.of(updatedUser);
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
