package com.vinylteam.vinyl.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.extractor.UserMapper;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.ServerException;
import com.vinylteam.vinyl.service.*;
import com.vinylteam.vinyl.web.dto.UserInfoRequest;
import com.vinylteam.vinyl.web.dto.UserSecurityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

@Service("googleUserService")
@RequiredArgsConstructor
@Slf4j
public class GoogleUserService implements ExternalUserService {

    @Value("${google.client-id}")
    private String clientId;

    private final UserService userService;

    private final UserDao userDao;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    private PasswordGenerator generator;

    @Autowired
    public void setGenerator(PasswordGenerator generator) {
        this.generator = generator;
    }

    @Override
    public UserSecurityResponse processExternalAuthorization(String token) throws GeneralSecurityException, IOException, ServerException {
        var googleIdToken = verifyToken(token);
        var response = new UserSecurityResponse();
        if (googleIdToken.isEmpty()) {
            return response;
        }
        var idToken = googleIdToken.get();
        Optional<String> newUserEmail = getEmailFromToken(idToken);
        if (newUserEmail.isEmpty()) {
            return response;
        }
        var appUser = getAppUserIfExists(idToken);
        if (appUser.isEmpty()) {//no user yet
            log.info("No user exists");
            String emailToRegister = newUserEmail.get();
            String newPassword = generator.generatePassword();
            UserInfoRequest registerRequest = UserInfoRequest.builder()
                    .email(emailToRegister)
                    .password(newPassword)
                    .passwordConfirmation(newPassword)
                    .build();
            appUser = userService.registerExternally(registerRequest);
        }
        var user = appUser.get();
        response.setMessage(user.getEmail());
        response.setUser(userMapper.mapUserToDto(user));
        var newTokenPair = jwtService.getTokenPair(userMapper.mapToDto(user));
        response.setTokenPair(newTokenPair);
        return response;
    }

    public Optional<GoogleIdToken> verifyToken(String token) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(clientId))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        return Optional.ofNullable(verifier.verify(token));
    }

    public Optional<User> getAppUserIfExists(GoogleIdToken idToken) {
        GoogleIdToken.Payload payload = idToken.getPayload();

        String userId = payload.getSubject();
        log.info("User ID: " + userId);

        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        if (emailVerified) {
            return userDao.findByEmail(email);
        }
        return Optional.empty();
    }

    public Optional<String> getEmailFromToken(GoogleIdToken idToken) {
        GoogleIdToken.Payload payload = idToken.getPayload();

        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        if (emailVerified) {
            return Optional.of(email);
        }
        return Optional.empty();
    }

    byte[] generateString() {
        byte[] salt = new byte[32];
        new Random().nextBytes(salt);
        log.debug("Generated byte array with google user password");
        return salt;
    }
}
