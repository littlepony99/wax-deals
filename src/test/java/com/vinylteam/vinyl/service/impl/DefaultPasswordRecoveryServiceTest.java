package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.PasswordRecoveryDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.PasswordRecoveryException;
import com.vinylteam.vinyl.exception.entity.ErrorPasswordRecovery;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class DefaultPasswordRecoveryServiceTest {

    @MockBean
    private PasswordRecoveryDao mockedPasswordRecoveryDao;
    @MockBean
    private UserService mockedUserService;
    @MockBean
    private MailSender mockedMailSender;
    @Autowired
    private DefaultPasswordRecoveryService passwordRecoveryService;
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeEach
    void beforeEach() {
        reset(mockedMailSender);
        reset(mockedPasswordRecoveryDao);
        reset(mockedUserService);
    }

    @Test
    @DisplayName("Add recovery user token")
    void addRecoveryUserToken() {
        //prepare
        long userId = 1L;
        //when
        RecoveryToken token = passwordRecoveryService.addRecoveryTokenWithUserId(userId);
        //then
        verify(mockedPasswordRecoveryDao).add(any());
        assertNotNull(token);
    }

    @Test
    @DisplayName("Add recovery user token if user id does not exist in the db")
    void addRecoveryUserTokenNonExistentUserId() {
        //prepare
        long userId = 2L;
        doThrow(DataIntegrityViolationException.class)
                .when(mockedPasswordRecoveryDao)
                .add(any());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.addRecoveryTokenWithUserId(userId));
        //then
        assertEquals(ErrorPasswordRecovery.ADD_TOKEN_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check for not null: null")
    void checkForIsNotEmptyNotNullNullString() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.checkForIsNotEmptyNotNull(null, ErrorPasswordRecovery.EMPTY_EMAIL));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check for not null: empty string")
    void checkForIsNotEmptyNotNullEmptyString() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.checkForIsNotEmptyNotNull("", ErrorPasswordRecovery.EMPTY_EMAIL));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check for not null: success check")
    void checkForIsNotEmptyNotNull() {
        //when
        assertDoesNotThrow(
                () -> passwordRecoveryService.checkForIsNotEmptyNotNull("correct value", ErrorPasswordRecovery.EMPTY_EMAIL)
        );
    }

    @Test
    @DisplayName("Check password: not null")
    void checkPasswordNotNull() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.checkPassword(null, "confirm password"));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: new password empty, confirm password does not match")
    void checkPasswordNewPasswordEmptyNotEqual() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.checkPassword("", "confirm password"));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: new password and confirm password are empty")
    void checkPasswordBothEmpty() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.checkPassword("", ""));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: new password is not empty but not equal to confirm password")
    void checkPasswordNotEmptyNotEqual() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.checkPassword("password", ""));
        //then
        assertEquals(ErrorPasswordRecovery.PASSWORDS_NOT_EQUAL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: new password and confirm password are not empty and equal")
    void checkPassword() {
        //when
        assertDoesNotThrow(
                () -> passwordRecoveryService.checkPassword("password", "password")
        );
    }

    @Test
    @DisplayName("Change password - new password is null")
    void changePasswordNullNewPassword() {
        //prepare
        UUID token = UUID.randomUUID();
        UserChangeProfileInfoRequest userProfileInfoNullPassword = UserChangeProfileInfoRequest.builder()
                .token(token.toString())
                .build();
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.changePassword(userProfileInfoNullPassword));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao, never()).findByToken(eq(token));
        verify(mockedUserService, never()).findById(anyLong());
        verify(mockedUserService, never()).update(any(), any(), any(), any());
        verify(mockedPasswordRecoveryDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Change password - new password is empty")
    void changePasswordEmptyNewPassword() {
        //prepare
        UUID token = UUID.randomUUID();
        UserChangeProfileInfoRequest userProfileInfoEmptyPassword = UserChangeProfileInfoRequest.builder()
                .newPassword("")
                .token(token.toString())
                .build();
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.changePassword(userProfileInfoEmptyPassword));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_PASSWORD.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao, never()).findByToken(eq(token));
        verify(mockedUserService, never()).findById(anyLong());
        verify(mockedUserService, never()).update(any(), any(), any(), any());
        verify(mockedPasswordRecoveryDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Change password - token not found in db")
    void changePasswordTokenNotCorrect() {
        //prepare
        UUID token = UUID.randomUUID();
        when(mockedPasswordRecoveryDao.findByToken(eq(token))).thenReturn(Optional.empty());
        UserChangeProfileInfoRequest userProfileInfoNonExistentToken = UserChangeProfileInfoRequest.builder()
                .newPassword("new_password")
                .confirmNewPassword("new_password")
                .token(token.toString())
                .build();
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.changePassword(userProfileInfoNonExistentToken));
        //then
        assertEquals(ErrorPasswordRecovery.TOKEN_NOT_FOUND_IN_DB.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao).findByToken(eq(token));
        verify(mockedUserService, never()).findById(anyLong());
        verify(mockedUserService, never()).update(any(), any(), any(), any());
        verify(mockedPasswordRecoveryDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Change password - error while updating user")
    void changePasswordUserUpdateError() {
        //prepare
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1);
        UUID token = recoveryToken.getToken();
        UserChangeProfileInfoRequest userProfileInfo = UserChangeProfileInfoRequest.builder()
                .newPassword("new_password")
                .confirmNewPassword("new_password")
                .token(token.toString())
                .build();
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        when(mockedUserService.findById(user.getId())).thenReturn(Optional.of(user));
        doThrow(DataIntegrityViolationException.class).when(mockedUserService).update(any(), any(), any(), any());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.changePassword(userProfileInfo));
        //then
        assertEquals(ErrorPasswordRecovery.UPDATE_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao).findByToken(eq(token));
        verify(mockedUserService).findById(anyLong());
        verify(mockedUserService).update(any(), any(), any(), any());
        verify(mockedPasswordRecoveryDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Change password - success")
    void changePasswordSuccess() {
        //prepare
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1);
        UUID token = recoveryToken.getToken();
        UserChangeProfileInfoRequest userProfileInfo = UserChangeProfileInfoRequest.builder()
                .newPassword("new_password")
                .confirmNewPassword("new_password")
                .token(token.toString())
                .build();
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        when(mockedUserService.findById(user.getId())).thenReturn(Optional.of(user));
        //when
        assertDoesNotThrow(
                () -> passwordRecoveryService.changePassword(userProfileInfo));
        //then
        verify(mockedPasswordRecoveryDao).findByToken(eq(token));
        verify(mockedUserService).findById(anyLong());
        verify(mockedUserService).update(any(), any(), any(), any());
        verify(mockedPasswordRecoveryDao).deleteById(anyLong());
    }

    @Test
    @DisplayName("Check token - token not found in db")
    void checkTokenNotFound() {
        //prepare
        UUID token = UUID.randomUUID();
        when(mockedPasswordRecoveryDao.findByToken(eq(token))).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.checkToken(token.toString()));
        //then
        assertEquals(ErrorPasswordRecovery.TOKEN_NOT_FOUND_IN_DB.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao).findByToken(eq(token));
    }

    @Test
    @DisplayName("Check token - expired, no error while deleting")
    void checkTokenExpiredSuccessfulDelete() {
        //prepare
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setToken(token);
        recoveryToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusHours(30)));
        recoveryToken.setId(1);
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.checkToken(token.toString()));
        //then
        assertEquals(ErrorPasswordRecovery.TOKEN_IS_EXPIRED.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao).findByToken(eq(token));
        verify(mockedPasswordRecoveryDao).deleteById(1);
    }

    @Test
    @DisplayName("Check token - expired, error while deleting")
    void checkTokenExpiredFailToDelete() {
        //prepare
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setToken(token);
        recoveryToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusHours(30)));
        recoveryToken.setId(1);
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        doThrow(DataIntegrityViolationException.class).when(mockedPasswordRecoveryDao).deleteById(1);
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.checkToken(token.toString()));
        //then
        assertEquals(ErrorPasswordRecovery.DELETE_TOKEN_ERROR.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao).findByToken(eq(token));
        verify(mockedPasswordRecoveryDao).deleteById(1);
    }

    @Test
    @DisplayName("Check token - success")
    void checkTokenSuccess() {
        //prepare
        UUID token = UUID.randomUUID();
        RecoveryToken recoveryToken = new RecoveryToken();
        recoveryToken.setToken(token);
        recoveryToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now().minusHours(3)));
        when(mockedPasswordRecoveryDao.findByToken(eq(token))).thenReturn(Optional.of(recoveryToken));
        //when
        assertDoesNotThrow(() -> passwordRecoveryService.checkToken(token.toString()));
        verify(mockedPasswordRecoveryDao).findByToken(eq(token));
    }

    @Test
    @DisplayName("Send email - error")
    void sendEmailWithLinkHasNotBeenSent() {
        //prepare
        doThrow(RuntimeException.class).when(mockedMailSender).sendMail(any(), any(), any());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.sendEmailWithLink("email", "token"));
        //then
        assertEquals(ErrorPasswordRecovery.EMAIL_SEND_ERROR.getMessage(), exception.getMessage());
        verify(mockedMailSender).sendMail(any(), any(), any());
    }

    @Test
    @DisplayName("Send email - success")
    void sendEmailWithLinkSuccess() {
        //when
        assertDoesNotThrow(
                () -> passwordRecoveryService.sendEmailWithLink("email", "token"));
        //then
        verify(mockedMailSender).sendMail(any(), any(), any());
    }


    @Test
    @DisplayName("Send link - email is null")
    void sendLinkEmailNull() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.sendLink(null));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_EMAIL.getMessage(), exception.getMessage());
        verify(mockedUserService, never()).findByEmail(null);
    }

    @Test
    @DisplayName("Send link - email is empty string")
    void sendLinkEmailEmpty() {
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.sendLink(""));
        //then
        assertEquals(ErrorPasswordRecovery.EMPTY_EMAIL.getMessage(), exception.getMessage());
        verify(mockedUserService, never()).findByEmail(eq(""));
    }

    @Test
    @DisplayName("Send link - user not found")
    void sendLinkUserNotFound() {
        //prepare
        String email = "not_existing@email";
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.sendLink(email));
        //then
        assertEquals(ErrorPasswordRecovery.EMAIL_NOT_FOUND_IN_DB.getMessage(), exception.getMessage());
        verify(mockedUserService).findByEmail(eq(email));
    }

    @Test
    @DisplayName("Send link - recovery token is not added")
    void sendLinkAddTokenError() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        doThrow(DataIntegrityViolationException.class).when(mockedPasswordRecoveryDao).add(any());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.sendLink(email));
        //then
        assertEquals(ErrorPasswordRecovery.ADD_TOKEN_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserService).findByEmail(eq(email));
        verify(mockedPasswordRecoveryDao).add(any());
    }

    @Test
    @DisplayName("Send link - email sending error")
    void sendLinkEmailSendError() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        doThrow(RuntimeException.class).when(mockedMailSender).sendMail(any(), any(), any());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.sendLink(email));
        //then
        assertEquals(ErrorPasswordRecovery.EMAIL_SEND_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserService).findByEmail(eq(email));
        verify(mockedPasswordRecoveryDao).add(any());
        verify(mockedMailSender).sendMail(any(), any(), any());
    }

    @Test
    @DisplayName("Send link - success")
    void sendLinkSuccess() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(Optional.of(user));
        //when
        assertDoesNotThrow(
                () -> passwordRecoveryService.sendLink(email));
        //then
        verify(mockedUserService).findByEmail(eq(email));
        verify(mockedPasswordRecoveryDao).add(any());
        verify(mockedMailSender).sendMail(any(), any(), any());
    }

    @Test
    @DisplayName("convert to UUID - correct value")
    void stringToUUID() {
        //prepare
        UUID uuid = UUID.randomUUID();
        //when
        UUID result = passwordRecoveryService.stringToUUID(uuid.toString());
        //then
        assertEquals(uuid, result);
    }

    @Test
    @DisplayName("convert to UUID - invalid value")
    void stringToUUIDInvalidString() {
        //prepare
        String invalidUUID = "not uuid";
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class,
                () -> passwordRecoveryService.stringToUUID(invalidUUID));
        //then
        assertEquals(ErrorPasswordRecovery.TOKEN_NOT_CORRECT_UUID.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Add recovery token with user id - existing user id, new token, success")
    void addRecoveryTokenWithUserId() {
        //prepare
        long userId = 1;
        //when
        assertDoesNotThrow(() -> passwordRecoveryService.addRecoveryTokenWithUserId(userId));
    }

    @Test
    @DisplayName("Add recovery token with user id - new user id, new token, DataIntegrityViolationException -> PasswordRecoveryException(ErrorPasswordRecovery.ADD_TOKEN_ERROR)")
    void addRecoveryTokenWithUserIdNonExistentUserId() {
        //prepare
        long nonExistentUserId = 2;
        doThrow(DataIntegrityViolationException.class).when(mockedPasswordRecoveryDao).add(any());
        //when
        Exception exception = assertThrows(PasswordRecoveryException.class, () -> passwordRecoveryService.addRecoveryTokenWithUserId(nonExistentUserId));
        //then
        assertEquals(ErrorPasswordRecovery.ADD_TOKEN_ERROR.getMessage(), exception.getMessage());
    }

}
