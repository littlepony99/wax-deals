package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.RecoveryPasswordDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.RecoveryPasswordException;
import com.vinylteam.vinyl.exception.entity.ErrorRecoveryPassword;
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

class DefaultRecoveryPasswordServiceTest {

    private final RecoveryPasswordDao mockedRecoveryPasswordDao = mock(RecoveryPasswordDao.class);
    private final UserService mockedUserService = mock(UserService.class);
    private final MailSender mockedMailSender = mock(MailSender.class);
    private final DefaultRecoveryPasswordService recoveryPasswordService = new DefaultRecoveryPasswordService(mockedRecoveryPasswordDao,
            mockedUserService, mockedMailSender, "http:/localhost:8080", 24);
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Test
    @DisplayName("Add recovery user token")
    void addRecoveryUserToken() {
        //prepare
        long userId = 1L;
        when(mockedRecoveryPasswordDao.add(any())).thenReturn(true);
        //when
        RecoveryToken token = recoveryPasswordService.addRecoveryUserToken(userId);
        //then
        assertNotNull(token);
    }

    @Test
    @DisplayName("Add recovery user token if user id is incorrect")
    void addRecoveryUserTokenIfUserIdIncorrect() {
        //prepare
        long userId = -1L;
        when(mockedRecoveryPasswordDao.add(any())).thenReturn(false);
        //when
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.addRecoveryUserToken(userId));
        //then
        assertEquals(ErrorRecoveryPassword.ADD_TOKEN_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check for not null: null")
    void checkForNotNull() {
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.checkForNotNull(null, ErrorRecoveryPassword.EMPTY_EMAIL));
        assertEquals(ErrorRecoveryPassword.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check for not null: empty string")
    void checkForNotNullEmptyString() {
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.checkForNotNull("", ErrorRecoveryPassword.EMPTY_EMAIL));
        assertEquals(ErrorRecoveryPassword.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check for not null: success check")
    void checkForNotNullSuccess() {
        assertDoesNotThrow(
                () -> recoveryPasswordService.checkForNotNull("correct value", ErrorRecoveryPassword.EMPTY_EMAIL)
        );
    }

    @Test
    @DisplayName("Check password: not null")
    void checkPasswordNotNull() {
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.checkPassword(null, "confirm password"));
        assertEquals(ErrorRecoveryPassword.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: not empty")
    void checkPasswordNotEmpty() {
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.checkPassword("", "confirm password"));
        assertEquals(ErrorRecoveryPassword.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: not empty even if both empty")
    void checkPasswordNotEmptyEqual() {
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.checkPassword("", ""));
        assertEquals(ErrorRecoveryPassword.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: not equal")
    void checkPasswordNotEqual() {
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.checkPassword("password", ""));
        assertEquals(ErrorRecoveryPassword.PASSWORDS_NOT_EQUAL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: correct")
    void checkPassword() {
        assertDoesNotThrow(
                () -> recoveryPasswordService.checkPassword("password", "password")
        );
    }

    @Test
    @DisplayName("Change password - password is null")
    void changePasswordNull() {
        //when
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.changePassword(null, null, "token"));
        //then
        assertEquals(ErrorRecoveryPassword.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Change password - password is empty")
    void changePasswordEmpty() {
        //when
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.changePassword("", null, "token"));
        //then
        assertEquals(ErrorRecoveryPassword.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Change password - token not found in db")
    void changePasswordTokenNotCorrect() {
        UUID token = UUID.randomUUID();
        when(mockedRecoveryPasswordDao.findByToken(token)).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.changePassword("new_password",
                        "new_password", token.toString()));
        //then
        assertEquals(ErrorRecoveryPassword.TOKEN_NOT_FOUND_IN_DB.getMessage(), exception.getMessage());
        verify(mockedUserService, never()).update(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Change password - error")
    void changePasswordError() {
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1);
        UUID token = recoveryToken.getToken();
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedRecoveryPasswordDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        when(mockedUserService.findById(user.getId())).thenReturn(Optional.of(user));
        when(mockedUserService.update(user.getEmail(), user.getEmail(), "new_password",
                user.getDiscogsUserName())).thenReturn(false);
        //when
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.changePassword("new_password",
                        "new_password", token.toString()));
        //then
        assertEquals(ErrorRecoveryPassword.UPDATE_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserService).update(user.getEmail(), user.getEmail(), "new_password",
                user.getDiscogsUserName());
    }

    @Test
    @DisplayName("Change password - success")
    void changePasswordSuccess() {
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1);
        UUID token = recoveryToken.getToken();
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedRecoveryPasswordDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        when(mockedUserService.findById(user.getId())).thenReturn(Optional.of(user));
        when(mockedUserService.update(user.getEmail(), user.getEmail(), "new_password",
                user.getDiscogsUserName())).thenReturn(true);
        //when
        assertDoesNotThrow(
                () -> recoveryPasswordService.changePassword("new_password",
                        "new_password", token.toString()));
        //then
        verify(mockedUserService).update(user.getEmail(), user.getEmail(), "new_password",
                user.getDiscogsUserName());
        verify(mockedRecoveryPasswordDao).deleteById(recoveryToken.getId());
    }

    @Test
    @DisplayName("Check token - token not found in db")
    void checkTokenNotFound() {
        UUID token = UUID.randomUUID();
        when(mockedRecoveryPasswordDao.findByToken(token)).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.checkToken(token.toString()));
        //then
        assertEquals(ErrorRecoveryPassword.TOKEN_NOT_FOUND_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check token - expired")
    void checkTokenExpired() {
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setToken(token);
        recoveryToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusHours(30)));
        recoveryToken.setId(1);
        when(mockedRecoveryPasswordDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        //when
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.checkToken(token.toString()));
        //then
        assertEquals(ErrorRecoveryPassword.TOKEN_IS_EXPIRED.getMessage(), exception.getMessage());
        verify(mockedRecoveryPasswordDao).deleteById(1);
    }

    @Test
    @DisplayName("Check token - success")
    void checkTokenSuccess() {
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setToken(token);
        recoveryToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusHours(3)));
        when(mockedRecoveryPasswordDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        //when
        assertDoesNotThrow(() -> recoveryPasswordService.checkToken(token.toString()));
    }

    @Test
    @DisplayName("Send email - error")
    void sendEmailWithLinkHasNotBeenSent() {
        when(mockedMailSender.sendMail(any(), any(), any())).thenReturn(false);
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.sendEmailWithLink("email", "token"));
        assertEquals(ErrorRecoveryPassword.EMAIL_SEND_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send email - success")
    void sendEmailWithLinkSuccess() {
        when(mockedMailSender.sendMail(any(), any(), any())).thenReturn(true);
        assertDoesNotThrow(
                () -> recoveryPasswordService.sendEmailWithLink("email", "token"));
    }


    @Test
    @DisplayName("Send link - email is null")
    void sendLinkEmailNull() {
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.sendLink(null));
        assertEquals(ErrorRecoveryPassword.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - email is empty string")
    void sendLinkEmailEmpty() {
        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.sendLink(""));
        assertEquals(ErrorRecoveryPassword.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - user not found")
    void sendLinkUserNotFound() {
        String email = "not_existing@email";
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.sendLink(email));
        assertEquals(ErrorRecoveryPassword.EMAIL_NOT_FOUND_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - recovery token is not added")
    void sendLinkAddTokenError() {
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockedRecoveryPasswordDao.add(any())).thenReturn(false);

        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.sendLink(email));
        assertEquals(ErrorRecoveryPassword.ADD_TOKEN_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - email sending error")
    void sendLinkEmailSendError() {
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockedRecoveryPasswordDao.add(any())).thenReturn(true);
        when(mockedMailSender.sendMail(any(), any(), any())).thenReturn(false);

        Exception exception = assertThrows(RecoveryPasswordException.class,
                () -> recoveryPasswordService.sendLink(email));
        assertEquals(ErrorRecoveryPassword.EMAIL_SEND_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Send link - success")
    void sendLinkSuccess() {
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockedRecoveryPasswordDao.add(any())).thenReturn(true);
        when(mockedMailSender.sendMail(any(), any(), any())).thenReturn(true);

        assertDoesNotThrow(
                () -> recoveryPasswordService.sendLink(email));
    }

    @Test
    @DisplayName("convert to UUID - correct value")
    void stringToUUD() {
        UUID uuid = UUID.randomUUID();
        UUID result = recoveryPasswordService.stringToUUD(uuid.toString());
        assertEquals(uuid, result);
    }
}