package com.vinylteam.vinyl.security.impl;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

@Slf4j
@Service
public class DefaultSecurityService implements SecurityService {

    private final Random random = new SecureRandom();
    private final SecretKeyFactory secretKeyFactory;

    private final String algorithm = "PBKDF2WithHmacSHA512";

    public DefaultSecurityService() {
        log.debug("Started initializer in DefaultSecurityService");
        try {
            secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error during initialisation of secretKeyFactory", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public User createUserWithHashedPassword(String email, char[] password) {
        byte[] salt = generateSalt();
        int iterations = 10000;
        String hashedPassword = hashPassword(password, salt, iterations);
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setSalt(Base64.getEncoder().encodeToString(salt));
        user.setIterations(iterations);
        user.setRole(Role.USER);
        user.setStatus(false);
        log.debug("Resulting user is {'user':{}}", user);
        return user;
    }

    @Override
    public boolean checkPasswordAgainstUserPassword(User user, char[] password) {
        boolean isSame = false;
        if (user != null) {
            isSame = (user.getPassword().equals(hashPassword(password,
                    Base64.getDecoder().decode(user.getSalt()), user.getIterations())));
            log.debug("Compared hash of passed password against user's hashed password");
        }
        log.debug("Result of comparing password against user's password is {'isSame': {}, 'user':{}}", isSame, user);
        return isSame;
    }

    String hashPassword(char[] password, byte[] salt, int iterations) {
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, iterations, 256);
            Arrays.fill(password, '\u0000');
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            log.debug("Generated secretKey from pbeKeySpeck");
            byte[] result = secretKey.getEncoded();
            log.debug("Encoded password into hash");
            return Base64.getEncoder().encodeToString(result);
        } catch (InvalidKeySpecException e) {
            log.error("Error during hashing password", e);
            throw new RuntimeException(e);
        }
    }

    byte[] generateSalt() {
        byte[] salt = new byte[20];
        random.nextBytes(salt);
        log.debug("Generated byte array with salt");
        return salt;
    }

}
