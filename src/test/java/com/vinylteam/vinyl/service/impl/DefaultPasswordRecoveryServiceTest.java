
/*
package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.RecoveryDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.RecoveryException;
import com.vinylteam.vinyl.exception.entity.ErrorRecovery;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.MailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultPasswordRecoveryServiceTest {

    private final PasswordRecoveryDao mockedPasswordRecoveryDao = mock(PasswordRecoveryDao.class);
    private final UserService mockedUserService = mock(UserService.class);
    private final MailSender mockedMailSender = mock(MailSender.class);
    private final DefaultPassRecoveryService passwordRecoService = new DefaultPassRecoveryService(mockedPassRecoveryDao,
            mockedUserService, mockedMailSender, "http:/localhost:8080", 24);
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Test
    @DisplayName("Add recovery user token")
    void addRecoveryUserToken() {
        //prepare
        long userId = 1L;
        when(mockedPasswordRecoveryDao.add(any())).thenReturn(true);
        //when
        RecoveryToken token = passwordRecoveryService.addRecoveryUserToken(userId);
        //then
        assertNotNull(token);
    }

    @Test
    @DisplayName("Add recovery user token if user id is incorrect")
    void addRecoveryUserTokenIfUserIdIncorrect() {
        //prepare
        long userId = -1L;
        when(mockedPasswordRecoveryDao.add(any())).thenReturn(false);
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.addRecoveryUserToken(userId));
        //then
        assertEquals(ErrorPasswordRecovery.ADD_TOKEN_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check for not null: null")
    void checkForNotNull() {
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.checkForNotNull(null, ErrorPassword
       Recovery.EMPTY_EMAIL));
        assertEquals(ErrorPasswordRecovery.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check for not null: empty string")
    void checkForNotNullEmptyString() {
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.checkForNotNull("", ErrorPassword
       Recovery.EMPTY_EMAIL));
        assertEquals(ErrorPasswordRecovery.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check for not null: success check")
    void checkForNotNullSuccess() {
        assertDoesNotThrow(
                () -> passwordRecovery
       Service.checkForNotNull("correct value", ErrorPassword
       Recovery.EMPTY_EMAIL)
        );
    }

    @Test
    @DisplayName("Check password: not null")
    void checkPasswordNotNull() {
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.checkPassword(null, "confirm password"));
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: not empty")
    void checkPasswordNotEmpty() {
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.checkPassword("", "confirm password"));
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: not empty even if both empty")
    void checkPasswordNotEmptyEqual() {
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.checkPassword("", ""));
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: not equal")
    void checkPasswordNotEqual() {
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.checkPassword("password", ""));
        assertEquals(ErrorPasswordRecovery.PASSWORDS_NOT_EQUAL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: correct")
    void checkPassword() {
        assertDoesNotThrow(
                () -> passwordRecovery
       Service.checkPassword("password", "password")
        );
    }

    @Test
    @DisplayName("Change password - password is null")
    void changePasswordNull() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.changePassword(null, null, "token"));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Change password - password is empty")
    void changePasswordEmpty() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.changePassword("", null, "token"));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Change password - token not found in db")
    void changePasswordTokenNotCorrect() {
        UUID token = UUID.randomUUID();
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.changePassword("new_password",
                        "new_password", token.toString()));
        //then
        assertEquals(ErrorPasswordRecovery.TOKEN_NOT_FOUND_IN_DB.getMessage(), exception.getMessage());
        verify(mockedUserService, never()).update(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Change password - error")
    void changePasswordError() {
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1);
        UUID token = recoveryToken.getToken();
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        when(mockedUserService.findById(user.getId())).thenReturn(Optional.of(user));
        when(mockedUserService.update(user.getEmail(), user.getEmail(), "new_password",
                user.getDiscogsUserName())).thenReturn(false);
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.changePassword("new_password",
                        "new_password", token.toString()));
        //then
        assertEquals(ErrorPasswordRecovery.UPDATE_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserService).update(user.getEmail(), user.getEmail(), "new_password",
                user.getDiscogsUserName());
    }

    @Test
    @DisplayName("Change password - success")
    void changePasswordSuccess() {
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1);
        UUID token = recoveryToken.getToken();
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        when(mockedUserService.findById(user.getId())).thenReturn(Optional.of(user));
        when(mockedUserService.update(user.getEmail(), user.getEmail(), "new_password",
                user.getDiscogsUserName())).thenReturn(true);
        //when
        assertDoesNotThrow(
                () -> passwordRecovery
       Service.changePassword("new_password",
                        "new_password", token.toString()));
        //then
        verify(mockedUserService).update(user.getEmail(), user.getEmail(), "new_password",
                user.getDiscogsUserName());
        verify(mockedPasswordRecoveryDao).deleteById(recoveryToken.getId());
    }

    @Test
    @DisplayName("Check token - token not found in db")
    void checkTokenNotFound() {
        UUID token = UUID.randomUUID();
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.checkToken(token.toString()));
        //then
        assertEquals(ErrorPasswordRecovery.TOKEN_NOT_FOUND_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check token - expired")
    void checkTokenExpired() {
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setToken(token);
        recoveryToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusHours(30)));
        recoveryToken.setId(1);
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.checkToken(token.toString()));
        //then
        assertEquals(ErrorPasswordRecovery.TOKEN_IS_EXPIRED.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao).deleteById(1);
    }

    @Test
    @DisplayName("Check token - success")
    void checkTokenSuccess() {
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setToken(token);
        recoveryToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusHours(3)));
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        //when
        assertDoesNotThrow(() -> passwordRecoveryService.checkToken(token.toString()));
    }

    @Test
    @DisplayName("Send email - error")
    void sendEmailWithLinkHasNotBeenSent() {
        when(mockedMailSender.sendMail(any(), any(), any())).thenReturn(false);
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.sendEmailWithLink("email", "token"));
        assertEquals(ErrorPasswordRecovery.EMAIL_SEND_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send email - success")
    void sendEmailWithLinkSuccess() {
        when(mockedMailSender.sendMail(any(), any(), any())).thenReturn(true);
        assertDoesNotThrow(
                () -> passwordRecovery
       Service.sendEmailWithLink("email", "token"));
    }


    @Test
    @DisplayName("Send link - email is null")
    void sendLinkEmailNull() {
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.sendLink(null));
        assertEquals(ErrorPasswordRecovery.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - email is empty string")
    void sendLinkEmailEmpty() {
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.sendLink(""));
        assertEquals(ErrorPasswordRecovery.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - user not found")
    void sendLinkUserNotFound() {
        String email = "not_existing@email";
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.sendLink(email));
        assertEquals(ErrorPasswordRecovery.EMAIL_NOT_FOUND_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - recovery token is not added")
    void sendLinkAddTokenError() {
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockedPasswordRecoveryDao.add(any())).thenReturn(false);

        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.sendLink(email));
        assertEquals(ErrorPasswordRecovery.ADD_TOKEN_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - email sending error")
    void sendLinkEmailSendError() {
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockedPasswordRecoveryDao.add(any())).thenReturn(true);
        when(mockedMailSender.sendMail(any(), any(), any())).thenReturn(false);

        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecovery
       Service.sendLink(email));
        assertEquals(ErrorPasswordRecovery.EMAIL_SEND_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - success")
    void sendLinkSuccess() {
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockedPasswordRecoveryDao.add(any())).thenReturn(true);
        when(mockedMailSender.sendMail(any(), any(), any())).thenReturn(true);

        assertDoesNotThrow(
                () -> passwordRecovery
       Service.sendLink(email));
    }

    @Test
    @DisplayName("convert to UUID - correct value")
    void stringToUUD() {
        UUID uuid = UUID.randomUUID();
        UUID result = passwordRecoveryService.stringToUUD(uuid.toString());
        assertEquals(uuid, result);
    }
}*/
