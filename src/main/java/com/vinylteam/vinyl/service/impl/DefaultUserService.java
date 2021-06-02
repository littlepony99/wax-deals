package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class DefaultUserService implements UserService {

    private final UserDao userDao;
    private final SecurityService securityService;

    public DefaultUserService(UserDao userDao, SecurityService securityService) {
        this.userDao = userDao;
        this.securityService = securityService;
    }

    @Override
    public boolean add(String email, String password, String discogsUserName) {
        boolean isAdded = false;
        if (email != null && password != null) {
            User userToAdd = securityService
                    .createUserWithHashedPassword(email, password.toCharArray(), discogsUserName);
            isAdded = userDao.add(userToAdd);
            log.debug("Attempted to add created user to db with boolean result {'isAdded':{}}",
                    isAdded);
        }
        log.debug("Result of attempting to add user, created from passed email and password" +
                " if both are not null is {'isAdded': {}, 'email':{}}", isAdded, email);
        return isAdded;
    }

    @Override
    public boolean delete(User user) {
        return userDao.delete(user);
    }

    @Override
    public boolean update(String oldEmail, String newEmail, String newPassword, String newDiscogsUserName) {
        boolean isUpdated = false;
        if (newEmail != null && newPassword != null && oldEmail != null) {
            User changedUser = securityService.createUserWithHashedPassword(newEmail, newPassword.toCharArray(), newDiscogsUserName);
            if (oldEmail.equals(newEmail)) {
                changedUser.setStatus(true);
            }
            isUpdated = userDao.update(oldEmail, changedUser);
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
    public Optional<User> getByEmail(String email) {
        Optional<User> optionalUser = Optional.empty();
        if (email != null) {
            optionalUser = userDao.getByEmail(email);
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
            Optional<User> optionalUserFromDataBase = userDao.getByEmail(email);
            log.debug("Got optional with user from db by email {'email':{}, 'optionalUser':{}}",
                    email, optionalUserFromDataBase);
            if (optionalUserFromDataBase.isPresent()) {
                if (securityService.checkPasswordAgainstUserPassword(
                        optionalUserFromDataBase.get(), password.toCharArray())) {
                    log.debug("Hashed password passed as argument matches hashed password " +
                            "of user by passed email {'email':{}}", email);
                    optionalUser = optionalUserFromDataBase;
                }
            }
        }
        return optionalUser;
    }

    @Override
    public Optional<User> findById(long id){
        Optional<User> optionalUser = Optional.empty();
        if (id > 0){
            optionalUser = userDao.findById(id);
            log.debug("Attempted to get optional with user found by id from db {'id':{}, 'optional':{}}", id, optionalUser);
        } else {
            log.error("Passed id is 0 or less, returning empty optional");
        }
        log.debug("Resulting optional is {'optional':{}}", optionalUser);
        return optionalUser;
    }

}
