package com.vinylteam.vinyl.security.impl;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.entity.UserErrors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultSecurityServiceTest {

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private DefaultSecurityService securityService;// = new DefaultSecurityService();
    private final String rightPassword = "existingUserRightPassword";
    private final String wrongPassword = "existingUserWrongPassword";
    private final String passwordToHash = "password";
    private byte[] salt;
    private final User existingUser = new User();

    @BeforeAll
    void beforeAll() {
        salt = securityService.generateSalt();
        existingUser.setEmail("testuser1@vinyl.com");
        existingUser.setPassword(securityService
                .hashPassword(rightPassword.toCharArray(), salt, 10000));
        existingUser.setSalt(Base64.getEncoder().encodeToString(salt));
        existingUser.setIterations(10000);
        existingUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("Checks if the result of hashing with 10000 iterations is the same for equal inputs.")
    void hashPasswordWithTenThousandIterationsTest() {
        String resultHash = securityService.hashPassword(passwordToHash.toCharArray(), salt, 10000);
        assertTrue(encoder.matches(passwordToHash, securityService.hashPassword(passwordToHash.toCharArray(), salt, 10000)));
    }

    @Test
    @DisplayName("Checks if comparing hashed right password against user's stored hash returns true.")
    void checkPasswordAgainstExistingUserPasswordWithRightPasswordTest() {
        assertTrue(securityService.validateIfPasswordMatches(
                existingUser, rightPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if comparing hashed wrong password against user's stored hash returns false.")
    void checkPasswordAgainstUserPasswordWithWrongPasswordTest() {
        assertFalse(securityService.validateIfPasswordMatches(
                existingUser, wrongPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if comparing hashed right password against null user returns false.")
    void checkPasswordAgainstUserPasswordNullUserTest() {
        assertFalse(securityService.validateIfPasswordMatches(
                null, rightPassword.toCharArray()));
    }

    @Test
    @DisplayName("validates passwords when password and confirmation password are equal and match format")
    void validatePassword() {
        //prepare
        String password = "Qwerty1234";
        //when
        securityService.validatePassword(password, password);
    }

    @Test
    @DisplayName("validates passwords when password and confirmation password are not equal")
    void validatePasswordPasswordNotConfirmed() {
        //prepare
        String password = "Qwerty1234";
        String incorrectConfirmationPassword = "Qwerty12345";
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> securityService.validatePassword(password, incorrectConfirmationPassword));
        //then
        assertEquals(UserErrors.PASSWORDS_NOT_EQUAL_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("checks format for password that matches all requirements")
    void checkFormatLatin() {
        //prepare
        String password = "Qwerty1234";
        //when
        securityService.validatePassword(password);
    }

    @Test
    @DisplayName("checks format for password that doesn't contain uppercase letters")
    void checkFormatNoUppercase() {
        //prepare
        String passwordNoUppercase = "qwerty1234";
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> securityService.validatePassword(passwordNoUppercase));
        //then
        assertEquals(UserErrors.INVALID_PASSWORD_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("checks format for password that doesn't contain lowercase letters")
    void checkFormatNoLowercase() {
        //prepare
        String passwordNoLowercase = "QWERTY1234";
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> securityService.validatePassword(passwordNoLowercase));
        //then
        assertEquals(UserErrors.INVALID_PASSWORD_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("checks format for password that doesn't contain lowercase letters")
    void checkFormatNoNumbers() {
        //prepare
        String passwordNoNumber = "Qwertyui";
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> securityService.validatePassword(passwordNoNumber));
        //then
        assertEquals(UserErrors.INVALID_PASSWORD_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("checks format for password that contain less than 8 characters")
    void checkFormatShorterThanRequired() {
        //prepare
        String passwordTooShort = "Qwe123";
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> securityService.validatePassword(passwordTooShort));
        //then
        assertEquals(UserErrors.INVALID_PASSWORD_ERROR.getMessage(), exception.getMessage());
    }

}
