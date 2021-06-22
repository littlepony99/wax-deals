
package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.security.impl.DefaultSecurityService;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.web.dto.UserChangeProfileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultUserServiceTest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final UserDao mockedUserDao = mock(UserDao.class);
    private final SecurityService mockedSecurityService = mock(DefaultSecurityService.class);
    private final ConfirmationService mockedConfirmationService = mock(DefaultConfirmationService.class);
    private final UserService userService = new DefaultUserService(mockedUserDao, mockedSecurityService, mockedConfirmationService);
    private final List<User> users = dataGenerator.getUsersList();
    private final List<ConfirmationToken> tokens = dataGenerator.getConfirmationTokensList();
    private final User mockedUser = mock(User.class);
    private final ModelAndView mockedModelAndView = mock(ModelAndView.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedUserDao);
        reset(mockedSecurityService);
        reset(mockedConfirmationService);
        reset(mockedModelAndView);
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
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(null), eq(password.toCharArray()));
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
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(email), any());
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
        when(mockedSecurityService.createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()))).thenReturn(users.get(0));
        when(mockedUserDao.add(users.get(0))).thenReturn(-1L);
        //when
        boolean actualIsAdded = userService.add(existingEmail, password, newDiscogsUserName);
        //then
        assertFalse(actualIsAdded);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()));
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
        when(mockedSecurityService.createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()))).thenReturn(users.get(0));
        when(mockedUserDao.add(users.get(0))).thenReturn(-1L);
        //when
        boolean actualIsAdded = userService.add(existingEmail, password, newDiscogsUserName);
        //then
        assertFalse(actualIsAdded);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(existingEmail), eq(password.toCharArray()));
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
        ConfirmationToken confirmationToken = new ConfirmationToken();
        when(mockedSecurityService.createUserWithHashedPassword(eq(newEmail), eq(password.toCharArray()))).thenReturn(users.get(1));
        when(mockedUserDao.add(users.get(1))).thenReturn(1L);
        confirmationToken.setToken(UUID.randomUUID());
        when(mockedConfirmationService.addByUserId(1L)).thenReturn(confirmationToken);
        when(mockedConfirmationService.sendMessageWithLinkToUserEmail(newEmail, confirmationToken.getToken().toString())).thenReturn(true);
        //when
        boolean actualIsAdded = userService.add(newEmail, password, newDiscogsUserName);
        //then
        assertTrue(actualIsAdded);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newEmail), eq(password.toCharArray()));
        verify(mockedUserDao).add(users.get(1));
    }

    @Test
    @DisplayName("Checks if .getByEmail(...) calls userDao.getByEmail() when email is not null and returns it's result")
    void getByEmailTest() {
        //prepare
        String email = "user1@wax-deals.com";
        Optional<User> optionalUserFromDB = Optional.of(users.get(0));
        when(mockedUserDao.findByEmail(email)).thenReturn(optionalUserFromDB);
        //when
        Optional<User> actualOptional = userService.findByEmail(email);
        //then
        assertEquals(optionalUserFromDB, actualOptional);
        verify(mockedUserDao).findByEmail(email);
    }

    @Test
    @DisplayName("Checks if .getByEmail(...) doesn't call userDao.getByEmail() when email is null and returns empty optional")
    void getByEmailNullEmailTest() {
        //when
        Optional<User> actualOptional = userService.findByEmail(null);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao, never()).findByEmail(null);
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
        verify(mockedUserDao, never()).findByEmail(null);
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
        verify(mockedUserDao, never()).findByEmail(email);
        verify(mockedSecurityService, never()).checkPasswordAgainstUserPassword(any(), eq(null));

    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with non-existent user's email and password returns Optional.empty()," +
            " userDao.getByEmail(...) is called and securityService.checkPasswordAgainstUserPassword(...) isn't called")
    void signInCheckNonExistingUserTest() {
        //prepare
        String newEmail = "user2@wax-deals.com";
        String newPassword = "password2";
        when(mockedUserDao.findByEmail(newEmail)).thenReturn(Optional.empty());
        //when
        Optional<User> actualOptional = userService.signInCheck(newEmail, newPassword);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao).findByEmail(newEmail);
        verify(mockedSecurityService, never()).checkPasswordAgainstUserPassword(eq(null), eq(newPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with existent user's email and wrong password as arguments returns Optional.empty()," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) are called")
    void signInCheckExistingNotVerifiedUserWrongPasswordTest() {
        //prepare
        String existingEmail = "user1@wax-deals.com";
        String rightPassword = "password1";
        User user = users.get(0);
        user.setId(1L);
        user.setStatus(false);
        Optional<User> optionalUserFromDB = Optional.of(user);
        ConfirmationToken confirmationToken = tokens.get(0);
        when(mockedUserDao.findByEmail(existingEmail)).thenReturn(optionalUserFromDB);
        when(mockedSecurityService.checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(rightPassword.toCharArray()))).thenReturn(true);
        when(mockedConfirmationService.findByUserId(1L)).thenReturn(Optional.of(confirmationToken));
        //when
        Optional<User> actualOptional = userService.signInCheck("user1@wax-deals.com", "password1");
        //then
        assertEquals(optionalUserFromDB, actualOptional);
        verify(mockedUserDao).findByEmail(existingEmail);
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(rightPassword.toCharArray()));
        verify(mockedConfirmationService).findByUserId(1L);
        verify(mockedConfirmationService).sendMessageWithLinkToUserEmail("user1@wax-deals.com", confirmationToken.getToken().toString());
    }

    @Test
    @DisplayName("Checks if .signInCheck(...) with existent user's email and wrong password as arguments returns Optional.empty()," +
            " userDao.getByEmail(...) and securityService.checkPasswordAgainstUserPassword(...) are called")
    void signInCheckExistingVerifiedUserWrongPasswordTest() {
        //prepare
        String existingEmail = "user1@wax-deals.com";
        String wrongPassword = "password3";
        Optional<User> optionalUserFromDB = Optional.of(dataGenerator.getUserWithNumber(1));
        when(mockedUserDao.findByEmail(existingEmail)).thenReturn(optionalUserFromDB);
        when(mockedSecurityService.checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(wrongPassword.toCharArray()))).thenReturn(false);
        //when
        Optional<User> actualOptional = userService.signInCheck(existingEmail, wrongPassword);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao).findByEmail(existingEmail);
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
        optionalUserFromDB.get().setId(1L);
        optionalUserFromDB.get().setStatus(true);
        when(mockedUserDao.findByEmail(existingEmail)).thenReturn(optionalUserFromDB);
        when(mockedSecurityService.checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(rightPassword.toCharArray()))).thenReturn(true);
        when(mockedConfirmationService.findByUserId(1L)).thenReturn(Optional.empty());
        //when
        Optional<User> actualOptional = userService.signInCheck("user1@wax-deals.com", "password1");
        //then
        assertEquals(optionalUserFromDB, actualOptional);
        verify(mockedUserDao).findByEmail(existingEmail);
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(eq(optionalUserFromDB.get()), eq(rightPassword.toCharArray()));
    }

    @Test
    @DisplayName("Incorrect token")
    void signInWithTokenNotCorrectToken() {
        UUID token = UUID.randomUUID();
        String existingEmail = "user1@wax-deals.com";
        String rightPassword = "password1";

        when(mockedConfirmationService.findByToken(token.toString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.signInCheck(existingEmail, rightPassword, token.toString()));
    }

    @Test
    @DisplayName("User found by token is different")
    void signInWithTokenNotCorrectUser() {
        String existingEmail = "user1@wax-deals.com";
        String rightPassword = "password1";

        Optional<ConfirmationToken> confirmationToken = Optional.of(tokens.get(1));
        Optional<User> optionalUser = Optional.of(users.get(1));
        String tokenAsString = confirmationToken.get().getToken().toString();

        when(mockedConfirmationService.findByToken(tokenAsString)).thenReturn(confirmationToken);
        when(mockedUserDao.findById(2L)).thenReturn(optionalUser);

        Optional<User> resultUser = userService.signInCheck(existingEmail, rightPassword, tokenAsString);

        assertTrue(resultUser.isEmpty());
    }

    @Test
    @DisplayName("Incorrect password")
    void signInWithTokenNotCorrectPassword() {

        String existingEmail = "user1@wax-deals.com";
        String wrongPassword = "password3";
        Optional<ConfirmationToken> confirmationToken = Optional.of(tokens.get(0));
        Optional<User> optionalUser = Optional.of(users.get(0));

        String tokenAsString = confirmationToken.get().getToken().toString();
        when(mockedConfirmationService.findByToken(tokenAsString)).thenReturn(confirmationToken);
        when(mockedUserDao.findById(1L)).thenReturn(optionalUser);
        when(mockedSecurityService.checkPasswordAgainstUserPassword(eq(optionalUser.get()), eq(wrongPassword.toCharArray()))).thenReturn(false);

        //when
        Optional<User> resultUser = userService.signInCheck(existingEmail, wrongPassword, tokenAsString);

        //then
        assertTrue(resultUser.isEmpty());
    }

    @Test
    @DisplayName("User found by token is different")
    void signInWithTokenSuccess() {
        String existingEmail = "user1@wax-deals.com";
        String rightPassword = "password1";
        Optional<ConfirmationToken> confirmationToken = Optional.of(tokens.get(0));
        Optional<User> optionalUser = Optional.of(dataGenerator.getUserWithNumber(1));

        String tokenAsString = confirmationToken.get().getToken().toString();
        when(mockedConfirmationService.findByToken(tokenAsString)).thenReturn(confirmationToken);
        when(mockedUserDao.findById(1L)).thenReturn(optionalUser);
        when(mockedSecurityService.checkPasswordAgainstUserPassword(eq(optionalUser.get()), eq(rightPassword.toCharArray())))
                .thenReturn(true);
        when(mockedConfirmationService.deleteByUserId(1L)).thenReturn(true);

        //when
        Optional<User> resultUser = userService.signInCheck(existingEmail, rightPassword, tokenAsString);

        //then
        assertTrue(resultUser.isPresent());
        assertEquals(optionalUser, resultUser);

        verify(mockedConfirmationService).deleteByUserId(1L);
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
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(newEmail), eq(password.toCharArray()));
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
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(null), eq(password.toCharArray()));
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
        verify(mockedSecurityService, never()).createUserWithHashedPassword(eq(newEmail), eq(null));
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
        when(mockedSecurityService.createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()))).thenReturn(mockedUser);
        when(mockedUserDao.update(eq(notExistingOldEmail), eq(mockedUser))).thenReturn(false);
        //when
        boolean isEdit = userService.update(notExistingOldEmail, newEmail, newPassword, newDiscogsUserName);
        //then
        assertFalse(isEdit);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newEmail), eq(newPassword.toCharArray()));
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
        when(mockedSecurityService.createUserWithHashedPassword(eq(newUser), eq(newPassword.toCharArray()))).thenReturn(mockedUser);
        when(mockedUserDao.update(eq(existingOldEmail), eq(mockedUser))).thenReturn(true);
        //when
        boolean isEdit = userService.update(existingOldEmail, newUser, newPassword, newDiscogsUserName);
        //then
        assertTrue(isEdit);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newUser), eq(newPassword.toCharArray()));
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
        when(mockedSecurityService.createUserWithHashedPassword(eq(newUser), eq(newPassword.toCharArray()))).thenReturn(mockedUser);
        when(mockedUserDao.update(eq(existingUser), eq(mockedUser))).thenReturn(true);
        //when
        boolean isEdit = userService.update(existingUser, newUser, newPassword, newDiscogsUserName);
        //then
        assertTrue(isEdit);
        verify(mockedSecurityService).createUserWithHashedPassword(eq(newUser), eq(newPassword.toCharArray()));
        verify(mockedUser).setStatus(true);
        verify(mockedUserDao).update(eq(existingUser), eq(mockedUser));
    }

    @Test
    @DisplayName("Checks that when id>0 UserDao.findById(id) is called, it's result is returned")
    void findById() {
        //prepare
        long id = 1;
        Optional<User> optionalUserFromDB = Optional.of(dataGenerator.getUserWithNumber(1));
        when(mockedUserDao.findById(id)).thenReturn(optionalUserFromDB);
        //when
        Optional<User> actualOptional = userService.findById(id);
        //then
        assertEquals(optionalUserFromDB, actualOptional);
        verify(mockedUserDao).findById(id);
    }

    @Test
    @DisplayName("Checks that when id>0 UserDao.findById(id) is called, it's result is returned")
    void findByInvalidId() {
        //prepare
        long id = -1;
        //when
        Optional<User> actualOptional = userService.findById(id);
        //then
        assertTrue(actualOptional.isEmpty());
        verify(mockedUserDao, never()).findById(id);
    }

    @Test
    @DisplayName("Check editProfile if newPassword and ConfirmNewPassword doesn't equals")
    void editProfileIfNewPasswordAndConfirmNewPasswordDoesNotEquals(){
        //prepare
        UserChangeProfileInfo userProfileInfo = dataGenerator.getUserChangeProfileInfo();
        userProfileInfo.setConfirmNewPassword("differentPassword");
        Optional<User> optionalUserFromDB = Optional.of(dataGenerator.getUserWithNumber(1));
        //when
        User userAfterEdit = userService.editProfile(userProfileInfo, optionalUserFromDB.get(), mockedModelAndView).orElse(new User());
        //then
        verify(mockedModelAndView).setStatus(HttpStatus.BAD_REQUEST);
        assertNotNull(userAfterEdit.getEmail());
        assertEquals(optionalUserFromDB.get(), userAfterEdit);
    }

    @Test
    @DisplayName("Checking editProfile if old password isn't correct")
    void editProfileIfOldPasswordIsNotCorrect(){
        //prepare
        UserChangeProfileInfo userProfileInfo = dataGenerator.getUserChangeProfileInfo();
        userProfileInfo.setOldPassword("incorrectPassword");
        Optional<User> optionalUserFromDB = Optional.of(dataGenerator.getUserWithNumber(1));
        when(mockedSecurityService.checkPasswordAgainstUserPassword(optionalUserFromDB.get(), userProfileInfo.getOldPassword().toCharArray())).thenReturn(false);
        //when
        User userAfterEdit = userService.editProfile(userProfileInfo, optionalUserFromDB.get(), mockedModelAndView).orElse(new User());
        //then
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(optionalUserFromDB.get(), userProfileInfo.getOldPassword().toCharArray());
        verify(mockedModelAndView).setStatus(HttpStatus.BAD_REQUEST);
        assertNotNull(userAfterEdit.getEmail());
        assertEquals(optionalUserFromDB.get(), userAfterEdit);
    }

    @Test
    @DisplayName("Checking editProfile if user didn't change password and update in db failed")
    void editProfileIfUserDidNotChangePasswordAndUpdateInDbFailed(){
        //prepare
        UserChangeProfileInfo userProfileInfo = dataGenerator.getUserChangeProfileInfo();
        userProfileInfo.setNewPassword("");
        userProfileInfo.setConfirmNewPassword("");

        User userFromDB = Optional.of(dataGenerator.getUserWithNumber(1)).get();
        String oldPassword = userFromDB.getPassword();
        String newEmail = userProfileInfo.getNewEmail();
        String newDiscogsUserName = userProfileInfo.getNewDiscogsUserName();
        userProfileInfo.setOldPassword(oldPassword);

        User userAfterEdit = dataGenerator.getUserWithNumber(1);
        userAfterEdit.setEmail(newEmail);
        userAfterEdit.setDiscogsUserName(newDiscogsUserName);

        when(mockedSecurityService.checkPasswordAgainstUserPassword(userFromDB, oldPassword.toCharArray())).thenReturn(true);
        when(mockedSecurityService.createUserWithHashedPassword(newEmail, oldPassword.toCharArray())).thenReturn(userAfterEdit);
        when(userService.update(userFromDB.getEmail(), userProfileInfo.getNewEmail(), oldPassword, userProfileInfo.getNewDiscogsUserName())).thenReturn(false);
        //when
        userAfterEdit = userService.editProfile(userProfileInfo, userFromDB, mockedModelAndView).orElse(new User());
        //then
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(userFromDB, oldPassword.toCharArray());
        verify(mockedModelAndView).setStatus(HttpStatus.BAD_REQUEST);
        assertNotNull(userAfterEdit.getEmail());
        assertEquals(userFromDB, userAfterEdit);
    }

    @Test
    @DisplayName("Checking editProfile if user didn't change password and update in db successfully")
    void editProfileIfUserDidNotChangePasswordAndUpdateInDbSuccessfully(){
        //prepare
        UserChangeProfileInfo userProfileInfo = dataGenerator.getUserChangeProfileInfo();
        userProfileInfo.setNewPassword("");
        userProfileInfo.setConfirmNewPassword("");
        User userFromDB = Optional.of(dataGenerator.getUserWithNumber(1)).get();
        String oldPassword = userFromDB.getPassword();
        String newEmail = userProfileInfo.getNewEmail();
        String newDiscogsUserName = userProfileInfo.getNewDiscogsUserName();
        userProfileInfo.setOldPassword(oldPassword);

        User userAfterEdit = dataGenerator.getUserWithNumber(1);
        userAfterEdit.setEmail(newEmail);
        userAfterEdit.setDiscogsUserName(newDiscogsUserName);

        when(mockedSecurityService.checkPasswordAgainstUserPassword(userFromDB, oldPassword.toCharArray())).thenReturn(true);
        when(mockedSecurityService.createUserWithHashedPassword(newEmail, oldPassword.toCharArray())).thenReturn(userAfterEdit);
        when(userService.update(userFromDB.getEmail(), newEmail, oldPassword, newDiscogsUserName)).thenReturn(true);
        when(mockedUserDao.findByEmail(newEmail)).thenReturn(Optional.of(userAfterEdit));
        when(userService.findByEmail(newEmail)).thenReturn(Optional.of(userAfterEdit));
        //when
        userAfterEdit = userService.editProfile(userProfileInfo, userFromDB, mockedModelAndView).orElse(new User());
        //then
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(userFromDB, oldPassword.toCharArray());
        verify(mockedModelAndView).setStatus(HttpStatus.SEE_OTHER);
        assertNotNull(userAfterEdit.getEmail());
        assertNotEquals(userFromDB, userAfterEdit);
    }

    @Test
    @DisplayName("Checking editProfile if user changed password and update in db failed")
    void editProfileIfUserChangedPasswordAndUpdateInDbFailed(){
        //prepare
        UserChangeProfileInfo userProfileInfo = dataGenerator.getUserChangeProfileInfo();
        User userFromDB = Optional.of(dataGenerator.getUserWithNumber(1)).get();
        String oldPassword = userFromDB.getPassword();
        String newEmail = userProfileInfo.getNewEmail();
        String newDiscogsUserName = userProfileInfo.getNewDiscogsUserName();
        userProfileInfo.setOldPassword(oldPassword);

        User userAfterEdit = dataGenerator.getUserWithNumber(1);
        userAfterEdit.setEmail(newEmail);
        userAfterEdit.setDiscogsUserName(newDiscogsUserName);

        when(mockedSecurityService.checkPasswordAgainstUserPassword(userFromDB, oldPassword.toCharArray())).thenReturn(true);
        when(mockedSecurityService.createUserWithHashedPassword(newEmail, userProfileInfo.getNewPassword().toCharArray())).thenReturn(userAfterEdit);
        when(userService.update(userFromDB.getEmail(), newEmail, userProfileInfo.getNewPassword(), newDiscogsUserName)).thenReturn(false);
        //when
        userAfterEdit = userService.editProfile(userProfileInfo, userFromDB, mockedModelAndView).orElse(new User());
        //then
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(userFromDB, oldPassword.toCharArray());
        verify(mockedModelAndView).setStatus(HttpStatus.BAD_REQUEST);
        assertNotNull(userAfterEdit.getEmail());
        assertEquals(userFromDB, userAfterEdit);
    }

    @Test
    @DisplayName("Checking editProfile if user changed password and update in db successfully")
    void editProfileIfUserChangedPasswordAndUpdateInDbSuccessfully(){
        //prepare
        UserChangeProfileInfo userProfileInfo = dataGenerator.getUserChangeProfileInfo();
        User userFromDB = Optional.of(dataGenerator.getUserWithNumber(1)).get();
        String oldPassword = userFromDB.getPassword();
        String newEmail = userProfileInfo.getNewEmail();
        String newDiscogsUserName = userProfileInfo.getNewDiscogsUserName();
        userProfileInfo.setOldPassword(oldPassword);

        User userAfterEdit = dataGenerator.getUserWithNumber(1);
        userAfterEdit.setEmail(newEmail);
        userAfterEdit.setDiscogsUserName(newDiscogsUserName);

        when(mockedSecurityService.checkPasswordAgainstUserPassword(userFromDB, oldPassword.toCharArray())).thenReturn(true);
        when(mockedSecurityService.createUserWithHashedPassword(newEmail, userProfileInfo.getNewPassword().toCharArray())).thenReturn(userAfterEdit);
        when(userService.update(userFromDB.getEmail(), newEmail, userProfileInfo.getNewPassword(), newDiscogsUserName)).thenReturn(true);
        when(mockedUserDao.findByEmail(newEmail)).thenReturn(Optional.of(userAfterEdit));
        when(userService.findByEmail(newEmail)).thenReturn(Optional.of(userAfterEdit));
        //when
        userAfterEdit = userService.editProfile(userProfileInfo, userFromDB, mockedModelAndView).orElse(new User());
        //then
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(userFromDB, oldPassword.toCharArray());
        verify(mockedModelAndView).setStatus(HttpStatus.SEE_OTHER);
        assertNotNull(userAfterEdit.getEmail());
        assertNotEquals(userFromDB, userAfterEdit);
    }

    @Test
    @DisplayName("Checking delete method if user is null")
    void deleteIfUserIsNull(){
        //when
        boolean isDeleted = userService.delete(null, mockedModelAndView);
        //then
        assertFalse(isDeleted);
    }

    @Test
    @DisplayName("Checking delete method if ModelAndView is null")
    void deleteIfModelAndViewIsNull(){
        //prepare
        User userFromDB = Optional.of(dataGenerator.getUserWithNumber(1)).get();
        //when
        boolean isDeleted = userService.delete(userFromDB, null);
        //then
        assertFalse(isDeleted);
    }

    @Test
    @DisplayName("Checking delete method if delete in db failed")
    void deleteIfDeleteInDbFailed(){
        //prepare
        User userFromDB = Optional.of(dataGenerator.getUserWithNumber(1)).get();
        when(mockedUserDao.delete(userFromDB)).thenReturn(false);
        //when
        boolean isDeleted = userService.delete(userFromDB, mockedModelAndView);
        //then
        verify(mockedModelAndView).setStatus(HttpStatus.BAD_REQUEST);
        assertFalse(isDeleted);
    }

    @Test
    @DisplayName("Checking delete method if delete in db successfully")
    void deleteIfDeleteInDbSuccessfully(){
        //prepare
        User userFromDB = Optional.of(dataGenerator.getUserWithNumber(1)).get();
        when(mockedUserDao.delete(userFromDB)).thenReturn(true);
        //when
        boolean isDeleted = userService.delete(userFromDB, mockedModelAndView);
        //then
        verify(mockedModelAndView).setStatus(HttpStatus.OK);
        assertTrue(isDeleted);
    }

}
