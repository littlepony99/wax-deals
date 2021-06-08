package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.RecoveryPasswordDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultRecoveryPasswordServiceTest {

    private final RecoveryPasswordDao mockedRecoveryPasswordDao = mock(RecoveryPasswordDao.class);
    private final UserService mockedUserService = mock(UserService.class);
    private final RecoveryPasswordService recoveryPasswordService = new DefaultRecoveryPasswordService(mockedRecoveryPasswordDao, mockedUserService);
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Test
    @DisplayName("Add recovery user token")
    void addRecoveryUserToken() {
        //prepare
        long userId = 1L;
        when(mockedRecoveryPasswordDao.addRecoveryUserToken(any())).thenReturn(true);
        //when
        String token = recoveryPasswordService.addRecoveryUserToken(userId);
        //then
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Add recovery user token if user id is incorrect")
    void addRecoveryUserTokenIfUserIdIncorrect() {
        //prepare
        long userId = -1L;
        when(mockedRecoveryPasswordDao.addRecoveryUserToken(any())).thenReturn(false);
        //when
        String token = recoveryPasswordService.addRecoveryUserToken(userId);
        //then
        assertTrue(token.isEmpty());
    }

    @Test
    @DisplayName("Get recovery user token by token")
    void getByRecoveryToken() {
        //prepare
        long userId = 1L;
        String token = "some-recovery-token";
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(userId);
        when(mockedRecoveryPasswordDao.getByRecoveryToken(token)).thenReturn(Optional.of(recoveryToken));
        //when
        Optional<RecoveryToken> optionalRecoveryToken = recoveryPasswordService.getByRecoveryToken(token);
        //then
        assertTrue(optionalRecoveryToken.isPresent());
        assertEquals(recoveryToken, optionalRecoveryToken.get());
    }

    @Test
    @DisplayName("Remove recovery user token by token")
    void removeRecoveryUserToken() {
        //prepare
        String token = "some-recovery-token";
        when(mockedRecoveryPasswordDao.removeRecoveryUserToken(token)).thenReturn(true);
        //when
        boolean isDeleted = recoveryPasswordService.removeRecoveryUserToken(token);
        //then
        assertTrue(isDeleted);
    }

    @Test
    @DisplayName("Get optional user by email")
    void getByEmail() {
        //prepare
        List<User> usersList = dataGenerator.getUsersList();
        User user = usersList.get(0);
        String email = "user1@wax-deals.com";
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        //when
        Optional<User> optionalUser = recoveryPasswordService.getByEmail(email);
        //then
        assertTrue(optionalUser.isPresent());
        assertEquals(user, optionalUser.get());
    }

    @Test
    @DisplayName("Get optional user by id")
    void findById() {
        //prepare
        List<User> usersList = dataGenerator.getUsersList();
        User user = usersList.get(0);
        long userId = 1L;
        when(mockedUserService.findById(userId)).thenReturn(Optional.of(user));
        //when
        Optional<User> optionalUser = recoveryPasswordService.findById(userId);
        //then
        assertTrue(optionalUser.isPresent());
        assertEquals(user, optionalUser.get());
    }

    @Test
    @DisplayName("Update user")
    void updateTest() {
        //prepare
        String oldEmail = "oldEmail@wax-deals.com";
        String newEmail = "nweEmail@wax-deals.com";
        String newPassword = "newPassword";
        String discogsUserName = "discogsUserName";
        when(mockedUserService.update(oldEmail, newEmail, newPassword, discogsUserName)).thenReturn(true);
        //when
        boolean isUpdated = recoveryPasswordService.update(oldEmail, newEmail, newPassword, discogsUserName);
        //then
        assertTrue(isUpdated);
    }

}