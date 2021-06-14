
/*
package com.vinylteam.vinyl.security.impl;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultSecurityServiceTest {

    private final DefaultSecurityService securityService = new DefaultSecurityService();
    private final String rightPassword = "existingUserRightPassword";
    private final String wrongPassword = "existingUserWrongPassword";
    private final String passwordToHash = "password";
    private final byte[] salt = securityService.generateSalt();
    private final User existingUser = new User();

    @BeforeAll
    void beforeAll() {
        existingUser.setEmail("testuser1@vinyl.com");
        existingUser.setPassword(securityService
                .hashPassword(rightPassword.toCharArray(), salt, 10000));
        existingUser.setSalt(Base64.getEncoder().encodeToString(salt));
        existingUser.setIterations(10000);
        existingUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("Checkes if the result of hashing with 10000 iterations is the same for equal inputs.")
    void hashPasswordWithTenThousandIterationsTest() {
        String resultHash = securityService.hashPassword(passwordToHash.toCharArray(), salt, 10000);
        assertEquals(resultHash,
                securityService.hashPassword(passwordToHash.toCharArray(), salt, 10000));
    }

    @Test
    @DisplayName("Checks if hashing with zero iterations throws an IllegalArgumentException.")
    void hashPasswordWithZeroIterationsTest() {
        assertThrows(IllegalArgumentException.class, () ->
                securityService.hashPassword(passwordToHash.toCharArray(), salt, 0));
    }

    @Test
    @DisplayName("Checks if comparing hashed right password against user's stored hash returns true.")
    void checkPasswordAgainstExistingUserPasswordWithRightPasswordTest() {
        assertTrue(securityService.checkPasswordAgainstUserPassword(
                existingUser, rightPassword.toCharArray()));
    }

    @Test
    @DisplayName("Checks if comparing hashed wrong password against user's stored hash returns false.")
    void checkPasswordAgainstUserPasswordWithWrongPasswordTest() {
        assertFalse(securityService.checkPasswordAgainstUserPassword(
                existingUser, wrongPassword.toCharArray()));
    }

    @Test

    @DisplayName("Checks if comparing hashed right password against null user returns false.")
    void checkPasswordAgainstUserPasswordNullUserTest() {
        assertFalse(securityService.checkPasswordAgainstUserPassword(
                null, rightPassword.toCharArray()));
    }

}*/
