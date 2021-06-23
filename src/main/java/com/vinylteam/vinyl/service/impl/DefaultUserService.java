package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
    public boolean add(String email, String password, String discogsUserName) {
        boolean isAdded = false;
        if (email != null && password != null) {
            User userToAdd = securityService
                    .createUserWithHashedPassword(email, password.toCharArray());
            userToAdd.setDiscogsUserName(discogsUserName);
            userDao.add(userToAdd);
            //TODO check how transaction annotation work after jdbc level repair
            //FIXME the user can be added to the database, but the letter has not been sent. Then the user will see a message that he cannot be added, but he will already be in the database. The mail just didn't send
            Optional<User> optionalUser = userDao.findByEmail(userToAdd.getEmail());
            log.debug("Added created user to db {'user':{}}", userToAdd);
            if (optionalUser.isPresent()) {
                ConfirmationToken confirmationToken = confirmationService.addByUserId(optionalUser.get().getId());
                isAdded = confirmationService.sendMessageWithLinkToUserEmail(userToAdd.getEmail(), confirmationToken.getToken().toString());
            }
        }
        log.debug("Result of attempting to add user, created from passed email and password" +
                " if both are not null is {'isAdded': {}, 'email':{}}", isAdded, email);
        return isAdded;
    }

    @Override
    public boolean delete(User user, ModelAndView modelAndView) {
        boolean isDeleted = false;
        if (user != null && modelAndView != null) {
            userDao.delete(user);
            if (isDeleted) {
                modelAndView.setStatus(HttpStatus.OK);
                log.debug("Set response status to {'status':{}}", HttpStatus.OK);
            } else {
                modelAndView.setViewName("editProfile");
                modelAndView.addObject("discogsUserName", user.getDiscogsUserName());
                modelAndView.addObject("email", user.getEmail());
                modelAndView.addObject("userRole", user.getRole().toString());
                modelAndView.setStatus(HttpStatus.BAD_REQUEST);
                log.debug("Set response status to {'status':{}}", HttpStatus.BAD_REQUEST);
                modelAndView.addObject("message", "Delete is fail! Try again!");
            }
        } else {
            log.error("At least one of passed to DefaultUserService.delete(...) arguments is null {'user': {}, 'modelAndView': {}}",
                    user == null ? "null" : user, modelAndView == null ? "null" : modelAndView);
        }
        return isDeleted;
    }

    @Override
    public Optional<User> findById(long id) {
        Optional<User> optionalUser = Optional.empty();
        if (id > 0) {
            optionalUser = userDao.findById(id);
            log.debug("Attempted to get optional with user found by id from db {'id':{}, 'optional':{}}", id, optionalUser);
        } else {
            log.error("Passed id is invalid, returning empty optional {'id':{}}", id);
        }
        log.debug("Resulting optional is {'optional':{}}", optionalUser);
        return optionalUser;
    }

    @Override
    public boolean update(String oldEmail, String newEmail, String newPassword, String newDiscogsUserName) {
        boolean isUpdated = false;
        if (newEmail != null && newPassword != null && oldEmail != null) {
            User changedUser = securityService.createUserWithHashedPassword(newEmail, newPassword.toCharArray());
            if (oldEmail.equalsIgnoreCase(newEmail)) {
                changedUser.setStatus(true);
            }
            changedUser.setDiscogsUserName(newDiscogsUserName);
            userDao.update(oldEmail, changedUser);
            log.debug("Attempt to update user with known email address in database with boolean result " +
                    "{'isUpdated':{}, 'oldEmail':{}}", isUpdated, oldEmail);
        } else {
            log.error("At least one of passed to DefaultUserService.update(...) arguments is null {'oldEmail': {}, 'newEmail': {}, {}'newDiscogsUserName': {}}",
                    oldEmail == null ? "null" : oldEmail, newEmail == null ? "null" : newEmail,
                    newPassword == null ? "'newPassword': null, " : "", newDiscogsUserName == null ? "null" : newDiscogsUserName);
        }
        return isUpdated;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> optionalUser = Optional.empty();
        if (email != null) {
            optionalUser = userDao.findByEmail(email);
            log.debug("Attempted to get optional with user found by email from db {'email':{}, 'optional':{}}", email, optionalUser);
        } else {
            log.error("Passed email is null, returning empty optional");
        }
        log.debug("Resulting optional is {'optional':{}}", optionalUser);
        return optionalUser;
    }

    @Override
    public Optional<User> signInCheck(String email, String password) {
        Optional<User> optionalUser = Optional.empty();
        if (email != null && password != null) {
            Optional<User> optionalUserFromDataBase = userDao.findByEmail(email);
            log.debug("Got optional with user from db by email {'email':{}, 'optionalUser':{}}",
                    email, optionalUserFromDataBase);
            if (optionalUserFromDataBase.isPresent()) {
                if (securityService.checkPasswordAgainstUserPassword(
                        optionalUserFromDataBase.get(), password.toCharArray())) {
                    log.debug("Hashed password passed as argument matches hashed password " +
                            "of user by passed email {'email':{}}", email);
                    optionalUser = optionalUserFromDataBase;
                    if (!optionalUser.get().getStatus()) {
                        Optional<ConfirmationToken> confirmationToken = confirmationService.findByUserId(optionalUser.get().getId());
                        confirmationToken.ifPresent(token -> {
                            try {
                                confirmationService.sendMessageWithLinkToUserEmail(email, token.getToken().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        }
        return optionalUser;
    }

    @Override
    public Optional<User> signInCheck(String email, String password, String token) {
        Optional<ConfirmationToken> optionalConfirmationTokenByLinkToken = confirmationService.findByToken(token);
        ConfirmationToken confirmationToken = optionalConfirmationTokenByLinkToken.orElseThrow(
                () -> new IllegalArgumentException("Sorry, something went wrong on our side, we're looking into it. Please, try to login again, or contact us."));
        User userByLinkToken = userDao.findById(confirmationToken.getUserId()).get();
        if (userByLinkToken.getEmail().equalsIgnoreCase(email)) {
            if (securityService.checkPasswordAgainstUserPassword(
                    userByLinkToken, password.toCharArray())) {
                confirmationService.deleteByUserId(userByLinkToken.getId());
                return Optional.of(userByLinkToken);
            }
        } else {
            log.error("Email is not correct, token was sent to {}", userByLinkToken.getEmail());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> editProfile(UserChangeProfileInfo userProfileInfo,
                                      User user,
                                      ModelAndView modelAndView) {
        String newPassword = userProfileInfo.getNewPassword();
        String newEmail = userProfileInfo.getNewEmail();
        String oldPassword = userProfileInfo.getOldPassword();
        String newDiscogsUserName = userProfileInfo.getNewDiscogsUserName();
        modelAndView.addObject("userRole", user.getRole().toString());
        String email = user.getEmail();
        String discogsUserName = user.getDiscogsUserName();
        if (!newPassword.equals(userProfileInfo.getConfirmNewPassword())) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
            modelAndView.addObject("message", "Sorry, passwords don't match!");
        } else {
            boolean checkOldPassword = securityService.checkPasswordAgainstUserPassword(user, oldPassword.toCharArray());
            if (checkOldPassword) {
                try {
                    if (!newPassword.isEmpty()) {
                        update(email, newEmail, newPassword, newDiscogsUserName);
                        log.debug("Trying update user with new email and new password in the database {'newEmail':{}}.", newEmail);
                    } else {
                        update(email, newEmail, oldPassword, newDiscogsUserName);
                        log.debug("Trying update user with new email and old password in the database {'newEmail':{}}.", newEmail);
                    }
                    log.info("User was updated with new email or password in the database {'newEmail':{}}.", newEmail);
                    modelAndView.setStatus(HttpStatus.SEE_OTHER);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_SEE_OTHER);
                    modelAndView.addObject("message", "Your profile is successfully changed.");
                    email = newEmail;
                    discogsUserName = newDiscogsUserName;
                    user = findByEmail(email).orElse(user);
                } catch (DataAccessException e) {
                    log.info("Failed to update with new email or password user in the database {'newEmail':{}}.", newEmail);
                    modelAndView.setStatus(HttpStatus.BAD_REQUEST);
                    log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                    modelAndView.addObject("message", "Edit is fail! Try again!");
                }
            } else {
                modelAndView.setStatus(HttpStatus.BAD_REQUEST);
                log.debug("Set response status to {'status':{}}", HttpServletResponse.SC_BAD_REQUEST);
                modelAndView.addObject("message", "Sorry, old password isn't correct!");
            }
        }
        modelAndView.addObject("discogsUserName", discogsUserName);
        modelAndView.addObject("email", email);
        return Optional.of(user);
    }

}
