package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultUserServiceTest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final UserDao mockedUserDao = mock(UserDao.class);
    private final SecurityService mockedSecurityService = mock(DefaultSecurityService.class);
    private final UserService userService = new DefaultUserService(mockedUserDao, mockedSecurityService);
    private final List<User> users = dataGenerator.getUsersList();
    private final User mockedUser = mock(User.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedUserDao);
        reset(mockedSecurityService);
    }

    @Test
    @DisplayName("Checks if .add(...) with null email returns false, securityService.createUserWithHashedPassword(...), userDao.add(...) aren't called")
    void addWithNullEmailTest() {
        //prepare
        String password = "password2";
        String newDiscogsUserName = "newDiscogsUserName";
        //when
        boolean actualIsAdded = userService.add(null, password, newDiscogsUserName);
        //then
        assertFalse(actualIsAdded);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(null), eq(password.toCharArray()), eq(newDiscogsUserName));
        verify(mockedUserDao, never()).add(any());
    }

    @Test
    @DisplayName("Checks if .add(...) with null password returns false, securityService.createUserWithHashedPassword(...), userDao.add(...) aren't called")
    void addWithNullPasswordTest() {
        //prepare
        String email = "user2@wax-deals.com";
        String newDiscogsUserName = "newDiscogsUserName";
        //when
        boolean actualIsAdded = userService.add(email, null, newDiscogsUserName);
        //then
        assertFalse(actualIsAdded);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(email), any(), eq(newDiscogsUserName));
        verify(mockedUserDao, never()).add(any());
    }

    @Test
    @DisplayName("Checks if .add(...) with already existing in database user's email and password returns false," +
            " securityService.createUserWithHashedPassword(...), userDao.add(...) are called")
    void addWithExistingEmailTest() {
        //prepare
        String existingEmail = "user1@wax-deals.com";
        String password = "password1";
        String newDiscogsUserName = "newDiscogsUserName";
        when(mockedSecurityService.createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()), eq(newDiscogsUserName))).thenReturn(users.get(0));
        when(mockedUserDao.add(users.get(0))).thenReturn(false);
        //when
        boolean actualIsAdded = userService.add(existingEmail, password, newDiscogsUserName);
        //then
        assertFalse(actualIsAdded);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()), eq(newDiscogsUserName));
        verify(mockedUserDao).add(users.get(0));
    }

    @Test
    @DisplayName("Checks if .add(...) with not existing in database user's email and password, but already existing " +
            "newDiscogs username returns false, securityService.createUserWithHashedPassword(...), userDao.add(...) are called")
    void addWithNotExistingEmailButAlreadyExistingDiscogsUserNameTest() {
        //prepare
        String existingEmail = "user123@wax-deals.com";
        String password = "password123";
        String newDiscogsUserName = "newDiscogsUserName1";
        when(mockedSecurityService.createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()), eq(newDiscogsUserName))).thenReturn(users.get(0));
        when(mockedUserDao.add(users.get(0))).thenReturn(false);
        //when
        boolean actualIsAdded = userService.add(existingEmail, password, newDiscogsUserName);
        //then
        assertFalse(actualIsAdded);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()), eq(newDiscogsUserName));
        verify(mockedUserDao).add(users.get(0));
    }

    @Test
    @DisplayName("Checks if .add(...) with not existing in database user's email and password returns true," +
            " securityService.createUserWithHashedPassword(...), userDao.add(...) are called")
    void addWithNewEmail() {
        //prepare
        String newEmail = "user2@wax-deals.com";
        String password = "password2";
        String newDiscogsUserName = "newDiscogsUserName";
        when(mockedSecurityService.createUserWithHashedPassword(eq(newEmail), eq(password.toCharArray()), eq(newDiscogsUserName))).thenReturn(users.get(1));
        when(mockedUserDao.add(users.get(1))).thenReturn(true);
        //when
        boolean actualIsAdded = userService.add(newEmail, password, newDiscogsUserName);
        //then
        assertTrue(actualIsAdded);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newEmail), eq(password.toCharArray()), eq(newDiscogsUserName));
        verify(mockedUserDao).add(users.get(1));
    }

    @Test
    @DisplayName("Checks if .getByEmail(...) calls userDao.getByEmail() when email is not null and returns it's result")
    void getByEmailTest() {
        //prepare
        String email = "user1@wax-deals.com";
        Optional optionalUserFromDB = Optional.of(users.get(0));
        when(mockedUserDao.getByEmail(email)).thenReturn(optionalUserFromDB);
        //when
        Optional actualOptional = userService.getByEmail(email);
        //then
        assertEquals(optionalUserFromDB, actualOptional);
        verify(mockedUserDao).getByEmail(email);
    }

    @Test
    @DisplayName("Checks if .getByEmail(...) doesn't call userDao.getByEmail() when email is null and returns empty optional")
    void getByEmailNullEmailTest() {
        //when
        Optional actualOptional = userService.getByEmail(null);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao, never()).getByEmail(null);
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with null email returns Optional.empty()," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) aren't called")
    void signInCheckNullEmailTest() {
        //prepare
        String password = "password1";
        //when
        Optional<User> actualOptional = userService.signInCheck(null, password);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao, never()).getByEmail(null);
        verify(mockedSecurityService, never()).checkPasswordAgainstUserPassword(any(), eq(password.toCharArray()));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with null password returns Optional.empty()," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) aren't called")
    void signInCheckNullPasswordTest() {
        //prepare
        String email = "user1@wax-deals.com";
        //when
        Optional<User> actualOptional = userService.signInCheck(email, null);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao, never()).getByEmail(email);
        verify(mockedSecurityService, never()).checkPasswordAgainstUserPassword(any(), eq(null));

    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with non-existent user's email and password returns Optional.empty()," +
            " userDao.getByEmail(...) is called and securityService.checkPasswordAgainstUserPassword(...) isn't called")
    void signInCheckNonExistingUserTest() {
        //prepare
        String newEmail = "user2@wax-deals.com";
        String newPassword = "password2";
        when(mockedUserDao.getByEmail(newEmail)).thenReturn(Optional.empty());
        //when
        Optional<User> actualOptional = userService.signInCheck(newEmail, newPassword);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao).getByEmail(newEmail);
        verify(mockedSecurityService, never()).checkPasswordAgainstUserPassword(eq(null), eq(newPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with existent user's email and wrong password as arguments returns Optional.empty()," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) are called")
    void signInCheckExistingVerifiedUserWrongPasswordTest() {
        //prepare
        String existingEmail = "user1@wax-deals.com";
        String wrongPassword = "password3";
        Optional<User> optionalUserFromDB = Optional.of(users.get(0));
        when(mockedUserDao.getByEmail(existingEmail)).thenReturn(optionalUserFromDB);
        when(mockedSecurityService.checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(wrongPassword.toCharArray()))).thenReturn(false);
        //when
        Optional<User> actualOptional = userService.signInCheck(existingEmail, wrongPassword);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao).getByEmail(existingEmail);
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(wrongPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with existent user's email and right password returns optional with user," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) are called")
    void signInCheckExistingVerifiedUserRightPasswordTest() {
        //prepare
        String existingEmail = "user1@wax-deals.com";
        String rightPassword = "password1";
        Optional<User> optionalUserFromDB = Optional.of(users.get(0));
        when(mockedUserDao.getByEmail(existingEmail)).thenReturn(optionalUserFromDB);
        when(mockedSecurityService.checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(rightPassword.toCharArray()))).thenReturn(true);
        //when
        Optional<User> actualOptional = userService.signInCheck("user1@wax-deals.com", "password1");
        //then
        assertEquals(optionalUserFromDB, actualOptional);
        verify(mockedUserDao).getByEmail(existingEmail);
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(rightPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if edit(...) with null old email as an argument returns false," +
            " securityService.createUserWithHashedPassword(...) and userDao.update(...) aren't called")
    void updateWhenOldEmailIsNullTest() {
        //prepare
        String newEmail = "newUser@wax-deals.com";
        String password = "newPassword";
        String newDiscogsUserName = "newDiscogsUserName";
        //when
        boolean isEdit = userService.update(null, newEmail, password, newDiscogsUserName);
        //then
        assertFalse(isEdit);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(newEmail), eq(password.toCharArray()), eq(newDiscogsUserName));
        verify(mockedUserDao, never()).update(eq(null), any());
    }

    @Test
    @DisplayName("Checks if edit(...) with null newEmail as an argument returns false," +
            " securityService.createUserWithHashedPassword(...) and userDao.update(...) aren't called")
    void updateWhenNewEmailIsNullTest() {
        //prepare
        String oldEmail = "oldEmail@wax-deals.com";
        String password = "newPassword";
        String newDiscogsUserName = "newDiscogsUserName";
        //when
        boolean isEdit = userService.update(oldEmail, null, password, newDiscogsUserName);
        //then
        assertFalse(isEdit);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(null), eq(password.toCharArray()), eq(newDiscogsUserName));
        verify(mockedUserDao, never()).update(eq(oldEmail), any());
    }

    @Test
    @DisplayName("Checks if edit(...) with null newPassword as an argument returns false," +
            " securityService.createUserWithHashedPassword(...) and userDao.update(...) aren't called")
    void updateWhenNewPasswordIsNullTest() {
        //prepare
        String oldEmail = "oldEmail@wax-deals.com";
        String newEmail = "newUser@wax-deals.com";
        String newDiscogsUserName = "newDiscogsUserName";
        //when
        boolean isEdit = userService.update(oldEmail, newEmail, null, newDiscogsUserName);
        //then
        assertFalse(isEdit);
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(newEmail), eq(null), eq(newDiscogsUserName));
        verify(mockedUserDao, never()).update(eq(oldEmail), any());
    }

    @Test
    @DisplayName("Returns false when user with oldEmil doesn't exist in db, newEmail!=oldEmail," +
            " securityService.createUserWithHashedPassword(...) and userDao.update(...) are called, User.setStatus(true) isn't.")
    void updateWhenUserDoesNotExistInDbTest() {
        //prepare
        String notExistingOldEmail = "nonExistentUser@wax-deals.com";
        String newEmail = "newUser@wax-deals.com";
        String newPassword = "newPassword";
        String newDiscogsUserName = "newDiscogsUserName";
        when(mockedSecurityService.createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()), eq(newDiscogsUserName))).thenReturn(mockedUser);
        when(mockedUserDao.update(eq(notExistingOldEmail), eq(mockedUser))).thenReturn(false);
        //when
        boolean isEdit = userService.update(notExistingOldEmail, newEmail, newPassword, newDiscogsUserName);
        //then
        assertFalse(isEdit);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()), eq(newDiscogsUserName));
        verify(mockedUser, never()).setStatus(true);
        verify(mockedUserDao).update(eq(notExistingOldEmail), eq(mockedUser));
    }

    @Test
    @DisplayName("Returns true when user with old email exists in db, newEmail!=oldEmail," +
            " securityService.createUserWithHashedPassword(...) and userDao.update(...) are called, User.setStatus(true) isn't.")
    void updateWhenUserExistsInDbTest() {
        //prepare
        String existingOldEmail = "existingUser@wax-deals.com";
        String newUser = "newUser@wax-deals.com";
        String newPassword = "newPassword";
        String newDiscogsUserName = "newDiscogsUserName";
        when(mockedSecurityService.createUserWithHashedPassword(eq(newUser), eq(newPassword.toCharArray()), eq(newDiscogsUserName))).thenReturn(mockedUser);
        when(mockedUserDao.update(eq(existingOldEmail), eq(mockedUser))).thenReturn(true);
        //when
        boolean isEdit = userService.update(existingOldEmail, newUser, newPassword, newDiscogsUserName);
        //then
        assertTrue(isEdit);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newUser), eq(newPassword.toCharArray()), eq(newDiscogsUserName));
        verify(mockedUser, never()).setStatus(true);
        verify(mockedUserDao).update(eq(existingOldEmail), eq(mockedUser));
    }

    @Test
    @DisplayName("Returns true when user exists in db, newEmail==oldEmail," +
            " securityService.createUserWithHashedPassword(...), userDao.update(...), and User.setStatus(true) are called")
    void updateWhenUserExistsInDbAndEmailWasNotChangedTest() {
        //prepare
        String existingUser = "existingUser@wax-deals.com";
        String newUser = "existingUser@wax-deals.com";
        String newPassword = "newPassword";
        String newDiscogsUserName = "newDiscogsUserName";
        when(mockedSecurityService.createUserWithHashedPassword(eq(newUser), eq(newPassword.toCharArray()), eq(newDiscogsUserName))).thenReturn(mockedUser);
        when(mockedUserDao.update(eq(existingUser), eq(mockedUser))).thenReturn(true);
        //when
        boolean isEdit = userService.update(existingUser, newUser, newPassword, newDiscogsUserName);
        //then
        assertTrue(isEdit);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newUser), eq(newPassword.toCharArray()), eq(newDiscogsUserName));
        verify(mockedUser).setStatus(true);
        verify(mockedUserDao).update(eq(existingUser), eq(mockedUser));
    }

}
