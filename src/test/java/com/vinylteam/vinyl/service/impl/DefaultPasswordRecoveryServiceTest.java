package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.PasswordRecoveryDao;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.exception.entity.PasswordRecoveryErrors;
import com.vinylteam.vinyl.exception.entity.UserErrors;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
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
    @DisplayName("Check password: new password is not empty but not equal to confirm password")
    void checkPasswordNotEmptyNotEqual() {
        //prepare
        UserInfoRequest request = UserInfoRequest.builder()
                .newPassword("password")
                .newPasswordConfirmation("")
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.changePassword(request));
        //then
        assertEquals(PasswordRecoveryErrors.PASSWORDS_NOT_EQUAL_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Check password: new password and confirm password are not empty and equal")
    void checkPassword() {
        //prepare
        UserInfoRequest request = UserInfoRequest.builder()
                .newPassword("password")
                .newPasswordConfirmation("password")
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.changePassword(request)
        );
        assertNotEquals(PasswordRecoveryErrors.PASSWORDS_NOT_EQUAL_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Change password - new password is null")
    void changePasswordNullNewPassword() throws ServerException {
        //prepare
        UUID token = UUID.randomUUID();
        UserInfoRequest userProfileInfoNullPassword = UserInfoRequest.builder()
                .token(token.toString())
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.changePassword(userProfileInfoNullPassword));
        //then
        assertEquals(PasswordRecoveryErrors.EMPTY_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao, never()).findByToken(eq(token));
        verify(mockedUserService, never()).findById(anyLong());
        verify(mockedUserService, never()).update(any(), any(), any(), any());
        verify(mockedPasswordRecoveryDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Change password - new password is empty")
    void changePasswordEmptyNewPassword() throws ServerException {
        //prepare
        UUID token = UUID.randomUUID();
        UserInfoRequest userProfileInfoEmptyPassword = UserInfoRequest.builder()
                .newPassword("")
                .token(token.toString())
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.changePassword(userProfileInfoEmptyPassword));
        //then
        assertEquals(PasswordRecoveryErrors.EMPTY_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao, never()).findByToken(eq(token));
        verify(mockedUserService, never()).findById(anyLong());
        verify(mockedUserService, never()).update(any(), any(), any(), any());
        verify(mockedPasswordRecoveryDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Change password - token not found in db")
    void changePasswordTokenNotCorrect() throws ServerException {
        //prepare
        UUID token = UUID.randomUUID();
        when(mockedPasswordRecoveryDao.findByToken(eq(token))).thenReturn(Optional.empty());
        UserInfoRequest userProfileInfoNonExistentToken = UserInfoRequest.builder()
                .newPassword("new_password")
                .newPasswordConfirmation("new_password")
                .token(token.toString())
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.changePassword(userProfileInfoNonExistentToken));
        //then
        assertEquals(PasswordRecoveryErrors.TOKEN_NOT_FOUND_IN_DB_ERROR.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao).findByToken(eq(token));
        verify(mockedUserService, never()).findById(anyLong());
        verify(mockedUserService, never()).update(any(), any(), any(), any());
        verify(mockedPasswordRecoveryDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Change password - error while updating user")
    void changePasswordUserUpdateError() throws ServerException {
        //prepare
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1);
        UUID token = recoveryToken.getToken();
        UserInfoRequest userProfileInfo = UserInfoRequest.builder()
                .newPassword("new_password")
                .newPasswordConfirmation("new_password")
                .token(token.toString())
                .build();
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedPasswordRecoveryDao.findByToken(token)).thenReturn(Optional.of(recoveryToken));
        when(mockedUserService.findById(user.getId())).thenReturn(Optional.of(user));
        doThrow(DataIntegrityViolationException.class).when(mockedUserService).update(any(), any(), any(), any());
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.changePassword(userProfileInfo));
        //then
        assertEquals(PasswordRecoveryErrors.UPDATE_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedPasswordRecoveryDao).findByToken(eq(token));
        verify(mockedUserService).findById(anyLong());
        verify(mockedUserService).update(any(), any(), any(), any());
        verify(mockedPasswordRecoveryDao, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Change password - success")
    void changePasswordSuccess() throws ServerException {
        //prepare
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(1);
        UUID token = recoveryToken.getToken();
        UserInfoRequest userProfileInfo = UserInfoRequest.builder()
                .newPassword("new_password")
                .newPasswordConfirmation("new_password")
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
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.checkToken(token.toString()));
        //then
        assertEquals(PasswordRecoveryErrors.TOKEN_NOT_FOUND_IN_DB_ERROR.getMessage(), exception.getMessage());
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
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.checkToken(token.toString()));
        //then
        assertEquals(PasswordRecoveryErrors.TOKEN_IS_EXPIRED_ERROR.getMessage(), exception.getMessage());
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
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.checkToken(token.toString()));
        //then
        assertEquals(PasswordRecoveryErrors.DELETE_TOKEN_ERROR.getMessage(), exception.getMessage());
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
    @DisplayName("Send link - email is null")
    void sendLinkEmailNull() {
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.sendLink(null));
        //then
        assertEquals(PasswordRecoveryErrors.EMPTY_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserService, never()).findByEmail(null);
    }

    @Test
    @DisplayName("Send link - email is empty string")
    void sendLinkEmailEmpty() {
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.sendLink(""));
        //then
        assertEquals(PasswordRecoveryErrors.EMPTY_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserService, never()).findByEmail(eq(""));
    }

    @Test
    @DisplayName("Send link - user not found")
    void sendLinkUserNotFound() {
        //prepare
        String email = "not_existing@email";
        when(mockedUserService.findByEmail(email)).thenThrow(new RuntimeException(UserErrors.EMAIL_NOT_FOUND_IN_DB_ERROR.getMessage()));
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.sendLink(email));
        //then
        assertEquals(PasswordRecoveryErrors.EMAIL_NOT_FOUND_IN_DB_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserService).findByEmail(eq(email));
    }

    @Test
    @DisplayName("Send link - recovery token is not added")
    void sendLinkAddTokenError() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(user);
        doThrow(DataIntegrityViolationException.class).when(mockedPasswordRecoveryDao).add(any());
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.sendLink(email));
        //then
        assertEquals(PasswordRecoveryErrors.ADD_TOKEN_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserService).findByEmail(eq(email));
        verify(mockedPasswordRecoveryDao).add(any());
    }

    @Test
    @DisplayName("Send link - email sending error")
    void sendLinkEmailSendError() throws ServerException {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(user);
        doThrow(ServerException.class).when(mockedMailSender).sendMail(any(), any(), any());
        //when
        Exception exception = assertThrows(ServerException.class,
                () -> passwordRecoveryService.sendLink(email));
        //then
        assertEquals(PasswordRecoveryErrors.EMAIL_SEND_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserService).findByEmail(eq(email));
        verify(mockedPasswordRecoveryDao).add(any());
        verify(mockedMailSender).sendMail(any(), any(), any());
    }

    @Test
    @DisplayName("Send link - success")
    void sendLinkSuccess() throws ServerException {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        when(mockedUserService.findByEmail(email)).thenReturn(user);
        //when
        assertDoesNotThrow(
                () -> passwordRecoveryService.sendLink(email));
        //then
        verify(mockedUserService).findByEmail(eq(email));
        verify(mockedPasswordRecoveryDao).add(any());
        verify(mockedMailSender).sendMail(any(), any(), any());
    }

    @Test
    @DisplayName("convert to UUID - invalid value")
    void stringToUUIDInvalidString() {
        //prepare
        String invalidUUID = "not uuid";
        UserInfoRequest request = UserInfoRequest.builder()
                .newPassword("new_password")
                .newPasswordConfirmation("new_password")
                .token(invalidUUID)
                .build();
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> passwordRecoveryService.changePassword(request));
        //then
        assertEquals(PasswordRecoveryErrors.TOKEN_NOT_CORRECT_UUID_ERROR.getMessage(), exception.getMessage());
    }

}
