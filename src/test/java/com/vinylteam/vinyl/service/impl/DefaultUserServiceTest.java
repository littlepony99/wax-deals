package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.entity.UserErrors;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.EmailConfirmationService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DefaultUserServiceTest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    @MockBean
    private UserDao mockedUserDao;
    @MockBean
    private SecurityService mockedSecurityService;
    @MockBean
    private EmailConfirmationService mockedEmailConfirmationService;
    @Autowired
    private DefaultUserService userService;

    @BeforeEach
    void beforeEach() {
        reset(mockedUserDao);
        reset(mockedSecurityService);
        reset(mockedEmailConfirmationService);
    }

    @Test
    @DisplayName("Registers user with valid fields")
    void register() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        User user = dataGenerator.getUserWithNumber(1);
        ConfirmationToken confirmationToken = dataGenerator.getConfirmationTokenWithUserId(1);
        when(mockedSecurityService.createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()))).thenReturn(user);
        when(mockedUserDao.add(user)).thenReturn(1L);
        when(mockedEmailConfirmationService.addByUserId(1L)).thenReturn(confirmationToken);
        //when
        userService.register(userInfo);
        //then
        verify(mockedSecurityService).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService).validatePassword(eq(userInfo.getPassword()), eq(userInfo.getPasswordConfirmation()));
        verify(mockedSecurityService).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao).add(user);
        verify(mockedEmailConfirmationService).addByUserId(user.getId());
        verify(mockedEmailConfirmationService).sendMessageWithLinkToUserEmail(user.getEmail(), confirmationToken.getToken().toString());
    }

    @Test
    @DisplayName("Registers user with null email")
    void registerNullEmail() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        userInfo.setEmail(null);
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.register(userInfo));
        //then
        assertEquals(UserErrors.EMPTY_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService, never()).emailFormatCheck(eq(null));
        verify(mockedSecurityService, never()).validatePassword(eq(userInfo.getPassword()), eq(userInfo.getPasswordConfirmation()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(null), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao, never()).add(any());
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(null), any());
    }

    @Test
    @DisplayName("Registers user with invalid email")
    void registerInvalidEmail() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        userInfo.setEmail("invalid Email");
        doThrow(new RuntimeException(UserErrors.INVALID_EMAIL_ERROR.getMessage())).when(mockedSecurityService).emailFormatCheck(userInfo.getEmail());
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.register(userInfo));
        //then
        assertEquals(UserErrors.INVALID_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService, never()).validatePassword(eq(userInfo.getPassword()), eq(userInfo.getPasswordConfirmation()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao, never()).add(any());
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(userInfo.getEmail()), anyString());
    }

    @Test
    @DisplayName("Registers user with duplicate email")
    void registerDuplicateEmail() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedSecurityService.createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()))).thenReturn(user);
        doThrow(DuplicateKeyException.class).when(mockedUserDao).add(user);
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.register(userInfo));
        //then
        assertEquals(UserErrors.ADD_USER_EXISTING_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).validatePassword(eq(userInfo.getPassword()), eq(userInfo.getPasswordConfirmation()));
        verify(mockedSecurityService).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao).add(user);
        verify(mockedEmailConfirmationService, never()).addByUserId(user.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(user.getEmail()), anyString());
    }

    @Test
    @DisplayName("Registers user with null password")
    void registerNullPassword() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        userInfo.setPassword(null);
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.register(userInfo));
        //then
        assertEquals(UserErrors.EMPTY_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService, never()).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService, never()).validatePassword(eq(userInfo.getPassword()), eq(userInfo.getPasswordConfirmation()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), any());
        verify(mockedUserDao, never()).add(any());
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(userInfo.getEmail()), anyString());
    }

    @Test
    @DisplayName("Registers user with invalid password")
    void registerInvalidPassword() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        userInfo.setPassword("no number");
        doThrow(new RuntimeException(UserErrors.INVALID_PASSWORD_ERROR.getMessage())).when(mockedSecurityService).validatePassword(userInfo.getPassword(), userInfo.getPasswordConfirmation());
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.register(userInfo));
        //then
        assertEquals(UserErrors.INVALID_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService).validatePassword(eq(userInfo.getPassword()), eq(userInfo.getPasswordConfirmation()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao, never()).add(any());
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(userInfo.getEmail()), anyString());
    }

    @Test
    @DisplayName("Registers user with password confirmation not matching password")
    void registerNotEqualPasswordConfirmation() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        userInfo.setPasswordConfirmation("not equal password confirmation");
        doThrow(new RuntimeException(UserErrors.PASSWORDS_NOT_EQUAL_ERROR.getMessage())).when(mockedSecurityService).validatePassword(userInfo.getPassword(), userInfo.getPasswordConfirmation());
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.register(userInfo));
        //then
        assertEquals(UserErrors.PASSWORDS_NOT_EQUAL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService).validatePassword(eq(userInfo.getPassword()), eq(userInfo.getPasswordConfirmation()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao, never()).add(any());
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(userInfo.getEmail()), anyString());
    }

    @Test
    @DisplayName("Finds user by existing email")
    void findByEmailTest() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedUserDao.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        //when
        userService.findByEmail(user.getEmail());
        //then
        verify(mockedUserDao).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Finds user by null email")
    void findByEmailNullEmailTest() {
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.findByEmail(null));
        //then
        assertEquals(UserErrors.EMPTY_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserDao, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Finds user by Non existent email")
    void findByEmailNonExistentEmailTest() {
        //prepare
        String nonExistentEmail = dataGenerator.getUserWithNumber(2).getEmail();
        when(mockedUserDao.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.findByEmail(nonExistentEmail));
        //then
        assertEquals(UserErrors.EMAIL_NOT_FOUND_IN_DB_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserDao).findByEmail(nonExistentEmail);
    }

    @Test
    @DisplayName("Finds user by existing id")
    void findByIdTest() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedUserDao.findById(user.getId())).thenReturn(Optional.of(user));
        //when
        userService.findById(user.getId());
        //then
        verify(mockedUserDao).findById(user.getId());
    }

    @Test
    @DisplayName("Finds user by Non existent id")
    void findByEmailNonExistentIdTest() {
        //prepare
        long nonExistentId = dataGenerator.getUserWithNumber(2).getId();
        when(mockedUserDao.findById(nonExistentId)).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.findById(nonExistentId));
        //then
        assertEquals(UserErrors.EMAIL_NOT_FOUND_IN_DB_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserDao).findById(nonExistentId);
    }

    @Test
    @DisplayName("Checks valid credentials for sign in")
    void signInCheck() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        User user = dataGenerator.getUserWithNumber(1);
        user.setStatus(true);
        when(mockedUserDao.findByEmail(userInfo.getEmail())).thenReturn(Optional.of(user));
        when(mockedSecurityService.validateIfPasswordMatches(user, userInfo.getPassword().toCharArray())).thenReturn(true);
        //when
        userService.signInCheck(userInfo);
        //then
        verify(mockedUserDao).findByEmail(userInfo.getEmail());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
    }

    @Test
    @DisplayName("Checks credentials for sign in when email is null")
    void signInCheckNullEmail() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        userInfo.setEmail(null);
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.signInCheck(userInfo));
        //then
        assertEquals(UserErrors.EMPTY_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserDao, never()).findByEmail(null);
        verify(mockedSecurityService, never()).validateIfPasswordMatches(any(), eq(userInfo.getPassword().toCharArray()));
    }

    @Test
    @DisplayName("Checks credentials for sign in when email is non-existent")
    void signInCheckNonExistentEmail() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        when(mockedUserDao.findByEmail(userInfo.getEmail())).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.signInCheck(userInfo));
        //then
        assertEquals(UserErrors.WRONG_CREDENTIALS_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserDao).findByEmail(userInfo.getEmail());
        verify(mockedSecurityService, never()).validateIfPasswordMatches(any(), eq(userInfo.getPassword().toCharArray()));
    }

    @Test
    @DisplayName("Checks credentials for sign in when password is null")
    void signInCheckNullPassword() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        userInfo.setPassword(null);
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.signInCheck(userInfo));
        //then
        assertEquals(UserErrors.EMPTY_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserDao, never()).findByEmail(userInfo.getEmail());
        verify(mockedSecurityService, never()).validateIfPasswordMatches(any(), any());
    }

    @Test
    @DisplayName("Checks credentials for sign in when password is incorrect")
    void signInCheckIncorrectPassword() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedUserDao.findByEmail(userInfo.getEmail())).thenReturn(Optional.of(user));
        when(mockedSecurityService.validateIfPasswordMatches(user, userInfo.getPassword().toCharArray())).thenReturn(false);
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.signInCheck(userInfo));
        //then
        assertEquals(UserErrors.WRONG_CREDENTIALS_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserDao).findByEmail(userInfo.getEmail());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
    }

    @Test
    @DisplayName("Checks credentials for sign in when user status is false")
    void signInCheckStatusFalse() {
        //prepare
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(1);
        User user = dataGenerator.getUserWithNumber(1);
        when(mockedUserDao.findByEmail(userInfo.getEmail())).thenReturn(Optional.of(user));
        when(mockedSecurityService.validateIfPasswordMatches(user, userInfo.getPassword().toCharArray())).thenReturn(true);
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.signInCheck(userInfo));
        //then
        assertEquals(UserErrors.EMAIL_NOT_VERIFIED_ERROR.getMessage(), exception.getMessage());
        verify(mockedUserDao).findByEmail(userInfo.getEmail());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
    }

    @Test
    @DisplayName("Tests that string isn't empty and isn't null with not empty string")
    void isNotEmptyNotNull() {
        //prepare
        String string = "not empty not null";
        //when
        boolean actualIsNotEmptyNotNull = userService.isNotEmptyNotNull(string);
        //then
        assertTrue(actualIsNotEmptyNotNull);
    }

    @Test
    @DisplayName("Tests that string isn't empty and isn't null with empty string")
    void isNotEmptyNotNullEmptyString() {
        //when
        boolean actualIsNotEmptyNotNull = userService.isNotEmptyNotNull("");
        //then
        assertFalse(actualIsNotEmptyNotNull);
    }

    @Test
    @DisplayName("Tests that string isn't empty and isn't null with null string")
    void isNotEmptyNotNullNullString() {
        //when
        boolean actualIsNotEmptyNotNull = userService.isNotEmptyNotNull(null);
        //then
        assertFalse(actualIsNotEmptyNotNull);

    }

    @Test
    @DisplayName("Updates user by old email with new email same as old email, new password, and new discogs user name")
    void update() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String email = user.getEmail();
        String newPassword = "Password123";
        String newDiscogsUserName = user.getDiscogsUserName();
        when(mockedSecurityService.createUserWithHashedPassword(eq(email), eq(newPassword.toCharArray()))).thenReturn(user);
        //when
        userService.update(email, email, newPassword, newDiscogsUserName);
        //then
        verify(mockedSecurityService).emailFormatCheck(email);
        verify(mockedSecurityService).validatePassword(newPassword);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(email), eq(newPassword.toCharArray()));
        assertTrue(user.getStatus());
        verify(mockedUserDao).update(email, user);
        verify(mockedUserDao, never()).findByEmail(email);
        verify(mockedEmailConfirmationService, never()).addByUserId(user.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(email), anyString());
    }

    @Test
    @DisplayName("Updates user by old email with new email different from old email, new password, and new discogs user name")
    void updateDifferentNewEmail() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String oldEmail = dataGenerator.getUserWithNumber(2).getEmail();
        String newEmail = user.getEmail();
        String newPassword = "Password123";
        String newDiscogsUserName = user.getDiscogsUserName();
        ConfirmationToken token = dataGenerator.getConfirmationTokenWithUserId(user.getId());
        when(mockedSecurityService.createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()))).thenReturn(user);
        when(mockedUserDao.findByEmail(newEmail)).thenReturn(Optional.of(user));
        when(mockedEmailConfirmationService.addByUserId(user.getId())).thenReturn(token);
        //when
        userService.update(oldEmail, newEmail, newPassword, newDiscogsUserName);
        //then
        verify(mockedSecurityService).emailFormatCheck(newEmail);
        verify(mockedSecurityService).validatePassword(newPassword);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()));
        assertFalse(user.getStatus());
        verify(mockedUserDao).update(oldEmail, user);
        verify(mockedUserDao).findByEmail(newEmail);
        verify(mockedEmailConfirmationService).addByUserId(user.getId());
        verify(mockedEmailConfirmationService).sendMessageWithLinkToUserEmail(eq(newEmail), eq(token.getToken().toString()));
    }

    @Test
    @DisplayName("Updates user by null old email with new email, new password, and new discogs user name")
    void updateNullOldEmail() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String oldEmail = null;
        String newEmail = user.getEmail();
        String newPassword = "Password123";
        String newDiscogsUserName = user.getDiscogsUserName();
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.update(oldEmail, newEmail, newPassword, newDiscogsUserName));
        //then
        assertEquals(UserErrors.EMPTY_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService, never()).emailFormatCheck(newEmail);
        verify(mockedSecurityService, never()).validatePassword(newPassword);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()));
        assertFalse(user.getStatus());
        verify(mockedUserDao, never()).update(oldEmail, user);
        verify(mockedUserDao, never()).findByEmail(newEmail);
        verify(mockedEmailConfirmationService, never()).addByUserId(user.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(newEmail), anyString());
    }

    @Test
    @DisplayName("Updates user by non-existent old email with new email, new password, and new discogs user name")
    void updateNonExistentOldEmail() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String oldEmail = dataGenerator.getUserWithNumber(2).getEmail();
        String newEmail = user.getEmail();
        String newPassword = "Password123";
        String newDiscogsUserName = user.getDiscogsUserName();
        when(mockedSecurityService.createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()))).thenReturn(user);
        when(mockedUserDao.findByEmail(newEmail)).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.update(oldEmail, newEmail, newPassword, newDiscogsUserName));
        //then
        assertEquals(UserErrors.EMAIL_NOT_FOUND_IN_DB_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).emailFormatCheck(newEmail);
        verify(mockedSecurityService).validatePassword(newPassword);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()));
        assertFalse(user.getStatus());
        verify(mockedUserDao).update(oldEmail, user);
        verify(mockedUserDao).findByEmail(newEmail);
        verify(mockedEmailConfirmationService, never()).addByUserId(user.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(newEmail), anyString());
    }

    @Test
    @DisplayName("Updates user by old email with null new email, new password, and new discogs user name")
    void updateNullNewEmail() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String oldEmail = dataGenerator.getUserWithNumber(2).getEmail();
        String newEmail = null;
        String newPassword = "Password123";
        String newDiscogsUserName = user.getDiscogsUserName();
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.update(oldEmail, newEmail, newPassword, newDiscogsUserName));
        //then
        assertEquals(UserErrors.EMPTY_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService, never()).emailFormatCheck(newEmail);
        verify(mockedSecurityService, never()).validatePassword(newPassword);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()));
        assertFalse(user.getStatus());
        verify(mockedUserDao, never()).update(oldEmail, user);
        verify(mockedUserDao, never()).findByEmail(newEmail);
        verify(mockedEmailConfirmationService, never()).addByUserId(user.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(newEmail), anyString());
    }

    @Test
    @DisplayName("Updates user by old email with invalid new email, new password, and new discogs user name")
    void updateInvalidNewEmail() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String oldEmail = dataGenerator.getUserWithNumber(2).getEmail();
        String newEmail = "Invalid email";
        String newPassword = "Password123";
        String newDiscogsUserName = user.getDiscogsUserName();
        doThrow(new RuntimeException(UserErrors.INVALID_EMAIL_ERROR.getMessage())).when(mockedSecurityService).emailFormatCheck(newEmail);
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.update(oldEmail, newEmail, newPassword, newDiscogsUserName));
        //then
        assertEquals(UserErrors.INVALID_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).emailFormatCheck(newEmail);
        verify(mockedSecurityService, never()).validatePassword(newPassword);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()));
        assertFalse(user.getStatus());
        verify(mockedUserDao, never()).update(oldEmail, user);
        verify(mockedUserDao, never()).findByEmail(newEmail);
        verify(mockedEmailConfirmationService, never()).addByUserId(user.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(newEmail), anyString());
    }

    @Test
    @DisplayName("Updates user by old email with new email, null new password, and new discogs user name")
    void updateNullNewPassword() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String oldEmail = dataGenerator.getUserWithNumber(2).getEmail();
        String newEmail = user.getEmail();
        String newPassword = null;
        String newDiscogsUserName = user.getDiscogsUserName();
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.update(oldEmail, newEmail, newPassword, newDiscogsUserName));
        //then
        assertEquals(UserErrors.EMPTY_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).emailFormatCheck(newEmail);
        verify(mockedSecurityService, never()).validatePassword(newPassword);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(newEmail), eq(null));
        assertFalse(user.getStatus());
        verify(mockedUserDao, never()).update(oldEmail, user);
        verify(mockedUserDao, never()).findByEmail(newEmail);
        verify(mockedEmailConfirmationService, never()).addByUserId(user.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(newEmail), anyString());
    }

    @Test
    @DisplayName("Updates user by old email with new email, invalid new password, and new discogs user name")
    void updateInvalidNewPassword() {
        //prepare
        User user = dataGenerator.getUserWithNumber(1);
        String oldEmail = dataGenerator.getUserWithNumber(2).getEmail();
        String newEmail = user.getEmail();
        String newPassword = "invalid password";
        String newDiscogsUserName = user.getDiscogsUserName();
        doThrow(new RuntimeException(UserErrors.INVALID_PASSWORD_ERROR.getMessage())).when(mockedSecurityService).validatePassword(newPassword);
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.update(oldEmail, newEmail, newPassword, newDiscogsUserName));
        //then
        assertEquals(UserErrors.INVALID_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).emailFormatCheck(newEmail);
        verify(mockedSecurityService).validatePassword(newPassword);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()));
        assertFalse(user.getStatus());
        verify(mockedUserDao, never()).update(oldEmail, user);
        verify(mockedUserDao, never()).findByEmail(newEmail);
        verify(mockedEmailConfirmationService, never()).addByUserId(user.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(newEmail), anyString());
    }

    @Test
    @DisplayName("edits profile of existing user with new email, new password, new discogs user name, with correct old password.")
    void editProfile() {
        User user = dataGenerator.getUserWithNumber(1);
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(2);
        User changedUser = dataGenerator.getUserWithNumber(2);
        changedUser.setId(2L);
        ConfirmationToken token = dataGenerator.getConfirmationTokenWithUserId(2);
        when(mockedSecurityService.validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()))).thenReturn(true);
        when(mockedSecurityService.createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getNewPassword().toCharArray())))
                .thenReturn(changedUser);
        when(mockedUserDao.findByEmail(changedUser.getEmail())).thenReturn(Optional.of(changedUser));
        when(mockedEmailConfirmationService.addByUserId(changedUser.getId())).thenReturn(token);
        //when
        User actualUser = userService.editProfile(userInfo, user);
        //then
        assertEquals(changedUser, actualUser);
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
        verify(mockedSecurityService).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService, times(2)).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService).validatePassword(eq(userInfo.getNewPassword()));
        verify(mockedSecurityService).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getNewPassword().toCharArray()));
        verify(mockedUserDao).update(eq(user.getEmail()), eq(changedUser));
        verify(mockedUserDao, times(2)).findByEmail(eq(userInfo.getEmail()));
        verify(mockedEmailConfirmationService).addByUserId(changedUser.getId());
        verify(mockedEmailConfirmationService).sendMessageWithLinkToUserEmail(eq(changedUser.getEmail()), eq(token.getToken().toString()));
    }

    @Test
    @DisplayName("edits profile of user with null old email with new email, new password, new discogs user name, with correct old password.")
    void editProfileNullOldEmail() {
        User user = dataGenerator.getUserWithNumber(1);
        user.setEmail(null);
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(2);
        when(mockedSecurityService.validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()))).thenReturn(true);
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.editProfile(userInfo, user));
        //then
        assertEquals(UserErrors.EMPTY_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
        verify(mockedSecurityService, never()).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService, never()).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService, never()).validatePassword(eq(userInfo.getNewPassword()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao, never()).update(eq(user.getEmail()), any());
        verify(mockedUserDao, never()).findByEmail(eq(userInfo.getEmail()));
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("edits profile of non-existent user with new email, new password, new discogs user name, with correct old password.")
    void editProfileNonExistentOldEmail() {
        User user = dataGenerator.getUserWithNumber(1);
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(2);
        User changedUser = dataGenerator.getUserWithNumber(2);
        changedUser.setId(2L);
        ConfirmationToken token = dataGenerator.getConfirmationTokenWithUserId(2);
        when(mockedSecurityService.validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()))).thenReturn(true);
        when(mockedSecurityService.createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getNewPassword().toCharArray())))
                .thenReturn(changedUser);
        when(mockedUserDao.findByEmail(changedUser.getEmail())).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.editProfile(userInfo, user));
        //then
        assertEquals(UserErrors.EMAIL_NOT_FOUND_IN_DB_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
        verify(mockedSecurityService).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService, times(2)).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService).validatePassword(eq(userInfo.getNewPassword()));
        verify(mockedSecurityService).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getNewPassword().toCharArray()));
        verify(mockedUserDao).update(eq(user.getEmail()), eq(changedUser));
        verify(mockedUserDao).findByEmail(eq(userInfo.getEmail()));
        verify(mockedEmailConfirmationService, never()).addByUserId(changedUser.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(changedUser.getEmail()), eq(token.getToken().toString()));
    }

    @Test
    @DisplayName("edits profile of existing user with new email, new password, new discogs user name, with null old password.")
    void editProfileNullOldPassword() {
        User user = dataGenerator.getUserWithNumber(1);
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(2);
        userInfo.setPassword(null);
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.editProfile(userInfo, user));
        //then
        assertEquals(UserErrors.EMPTY_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService, never()).validateIfPasswordMatches(eq(user), eq(null));
        verify(mockedSecurityService, never()).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService, never()).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService, never()).validatePassword(eq(userInfo.getNewPassword()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(null));
        verify(mockedUserDao, never()).update(eq(user.getEmail()), any());
        verify(mockedUserDao, never()).findByEmail(eq(userInfo.getEmail()));
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("edits profile of existing user with new email, new password, new discogs user name, with incorrect old password.")
    void editProfileIncorrectOldPassword() {
        User user = dataGenerator.getUserWithNumber(1);
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(2);
        when(mockedSecurityService.validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()))).thenReturn(false);
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.editProfile(userInfo, user));
        //then
        assertEquals(UserErrors.WRONG_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
        verify(mockedSecurityService, never()).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService, never()).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService, never()).validatePassword(eq(userInfo.getNewPassword()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao, never()).update(eq(user.getEmail()), any());
        verify(mockedUserDao, never()).findByEmail(eq(userInfo.getEmail()));
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("edits profile of existing user with new email, invalid new password, new discogs user name, with correct old password.")
    void editProfileInvalidNewPassword() {
        User user = dataGenerator.getUserWithNumber(1);
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(2);
        when(mockedSecurityService.validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()))).thenReturn(true);
        doThrow(new RuntimeException(UserErrors.INVALID_PASSWORD_ERROR.getMessage())).when(mockedSecurityService).validatePassword(eq(userInfo.getNewPassword()), eq(userInfo.getNewPasswordConfirmation()));
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.editProfile(userInfo, user));
        //then
        assertEquals(UserErrors.INVALID_PASSWORD_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
        verify(mockedSecurityService).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService, never()).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService, never()).validatePassword(eq(userInfo.getNewPassword()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao, never()).update(eq(user.getEmail()), any());
        verify(mockedUserDao, never()).findByEmail(eq(userInfo.getEmail()));
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("edits profile of existing user with new email, new password, new discogs user name, with correct old password and new password confirmation not equal new password.")
    void editProfileNotEqualNewPasswordConfirmation() {
        User user = dataGenerator.getUserWithNumber(1);
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(2);
        userInfo.setNewPasswordConfirmation("not equal");
        when(mockedSecurityService.validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()))).thenReturn(true);
        doThrow(new RuntimeException(UserErrors.PASSWORDS_NOT_EQUAL_ERROR.getMessage())).when(mockedSecurityService).validatePassword(eq(userInfo.getNewPassword()), eq(userInfo.getNewPasswordConfirmation()));
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.editProfile(userInfo, user));
        //then
        assertEquals(UserErrors.PASSWORDS_NOT_EQUAL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
        verify(mockedSecurityService).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService, never()).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService, never()).validatePassword(eq(userInfo.getNewPassword()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao, never()).update(eq(user.getEmail()), any());
        verify(mockedUserDao, never()).findByEmail(eq(userInfo.getEmail()));
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("edits profile of existing user with invalid new email, new password, new discogs user name, with correct old password.")
    void editProfileInvalidNewEmail() {
        User user = dataGenerator.getUserWithNumber(1);
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(2);
        when(mockedSecurityService.validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()))).thenReturn(true);
        doThrow(new RuntimeException(UserErrors.INVALID_EMAIL_ERROR.getMessage())).when(mockedSecurityService).emailFormatCheck(eq(userInfo.getEmail()));
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.editProfile(userInfo, user));
        //then
        assertEquals(UserErrors.INVALID_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
        verify(mockedSecurityService).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService, never()).validatePassword(eq(userInfo.getNewPassword()));
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao, never()).update(eq(user.getEmail()), any());
        verify(mockedUserDao, never()).findByEmail(eq(userInfo.getEmail()));
        verify(mockedEmailConfirmationService, never()).addByUserId(anyLong());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("edits profile of existing user with new email that has duplicate in database, new password, new discogs user name, with correct old password.")
    void editProfileDuplicateNewEmail() {
        User user = dataGenerator.getUserWithNumber(1);
        UserInfoRequest userInfo = dataGenerator.getUserInfoRequestWithNumber(2);
        User changedUser = dataGenerator.getUserWithNumber(2);
        changedUser.setId(2L);
        ConfirmationToken token = dataGenerator.getConfirmationTokenWithUserId(2);
        when(mockedSecurityService.validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()))).thenReturn(true);
        when(mockedSecurityService.createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getNewPassword().toCharArray())))
                .thenReturn(changedUser);
        doThrow(DuplicateKeyException.class).when(mockedUserDao).update(eq(user.getEmail()), eq(changedUser));
        //when
        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.editProfile(userInfo, user));
        //then
        assertEquals(UserErrors.UPDATE_USER_EXISTING_EMAIL_ERROR.getMessage(), exception.getMessage());
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
        verify(mockedSecurityService).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService, times(2)).emailFormatCheck(eq(userInfo.getEmail()));
        verify(mockedSecurityService).validatePassword(eq(userInfo.getNewPassword()));
        verify(mockedSecurityService).createUserWithHashedPassword(eq(userInfo.getEmail()), eq(userInfo.getNewPassword().toCharArray()));
        verify(mockedUserDao).update(eq(user.getEmail()), eq(changedUser));
        verify(mockedUserDao, never()).findByEmail(eq(userInfo.getEmail()));
        verify(mockedEmailConfirmationService, never()).addByUserId(changedUser.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(changedUser.getEmail()), eq(token.getToken().toString()));
    }

    @Test
    @DisplayName("edits profile of existing user with empty new email, empty new password, empty new discogs user name, with correct old password.")
    void editProfileAllFieldsEmpty() {
        User user = dataGenerator.getUserWithNumber(1);
        user.setId(1L);
        UserInfoRequest userInfo = UserInfoRequest.builder()
                .password("Password1234")
                .build();
        when(mockedSecurityService.validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()))).thenReturn(true);
        when(mockedSecurityService.createUserWithHashedPassword(eq(user.getEmail()), eq(userInfo.getPassword().toCharArray())))
                .thenReturn(user);
        when(mockedUserDao.findByEmail(eq(user.getEmail()))).thenReturn(Optional.of(user));
        //when
        User actualUser = userService.editProfile(userInfo, user);
        //then
        assertEquals(user, actualUser);
        verify(mockedSecurityService).validateIfPasswordMatches(eq(user), eq(userInfo.getPassword().toCharArray()));
        verify(mockedSecurityService, never()).validatePassword(userInfo.getNewPassword(), userInfo.getNewPasswordConfirmation());
        verify(mockedSecurityService).emailFormatCheck(eq(user.getEmail()));
        verify(mockedSecurityService).validatePassword(eq(userInfo.getPassword()));
        verify(mockedSecurityService).createUserWithHashedPassword(eq(user.getEmail()), eq(userInfo.getPassword().toCharArray()));
        verify(mockedUserDao).update(eq(user.getEmail()), eq(user));
        verify(mockedUserDao).findByEmail(eq(user.getEmail()));
        verify(mockedEmailConfirmationService, never()).addByUserId(user.getId());
        verify(mockedEmailConfirmationService, never()).sendMessageWithLinkToUserEmail(eq(user.getEmail()), anyString());
    }

    @Test
    @DisplayName("confirmEmailByToken when token exists in the db")
    void confirmEmailByTokenTest() {
        //prepare
        String token = UUID.randomUUID().toString();
        when(mockedEmailConfirmationService.findByToken(token)).thenReturn(Optional.of(dataGenerator.getConfirmationTokenWithUserId(1)));
        when(mockedUserDao.findById(1)).thenReturn(Optional.of(dataGenerator.getUserWithNumber(1)));
        //when
        userService.confirmEmailByToken(token);
        //then
        verify(mockedEmailConfirmationService).findByToken(token);
        verify(mockedUserDao).findById(1);
        verify(mockedEmailConfirmationService).deleteByUserId(1);
        verify(mockedUserDao).setUserStatusTrue(1);
    }

}

