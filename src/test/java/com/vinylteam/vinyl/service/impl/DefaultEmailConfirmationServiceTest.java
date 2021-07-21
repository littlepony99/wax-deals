package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.exception.entity.EmailConfirmationErrors;
import com.vinylteam.vinyl.service.EmailConfirmationService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.MailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class DefaultEmailConfirmationServiceTest {

    @MockBean
    private ConfirmationTokenDao mockedConfirmationDao;
    @MockBean
    private MailSender mockedMailSender;
    @Autowired
    private EmailConfirmationService confirmationService;
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeEach
    void beforeEach() {
        reset(mockedConfirmationDao);
        reset(mockedMailSender);
    }

    @Test
    @DisplayName("findByToken - token is not correct")
    void findByTokenNotCorrectString() {
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> confirmationService.findByToken("wrong value"));
        //then
        assertEquals(EmailConfirmationErrors.TOKEN_FROM_LINK_NOT_UUID.getMessage(), exception.getMessage());
        verify(mockedConfirmationDao, never()).findByToken(any());
    }

    @Test
    @DisplayName("findByToken - token is not found")
    void findByTokenNonExistentToken() {
        //prepare
        UUID token = UUID.randomUUID();
        when(mockedConfirmationDao.findByToken(eq(token))).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> confirmationService.findByToken(token.toString()));
        //then
        assertEquals(EmailConfirmationErrors.TOKEN_FROM_LINK_NOT_FOUND.getMessage(), exception.getMessage());
        verify(mockedConfirmationDao).findByToken(eq(token));
    }

    @Test
    @DisplayName("findByToken - token is found")
    void findByToken() {
        //prepare
        ConfirmationToken confirmationToken = dataGenerator.getConfirmationTokenWithUserId(1);
        when(mockedConfirmationDao.findByToken(eq(confirmationToken.getToken()))).thenReturn(Optional.of(confirmationToken));
        //when
        Optional<ConfirmationToken> optionalConfirmationToken = confirmationService.findByToken(confirmationToken.getToken().toString());
        //then
        assertEquals(confirmationToken, optionalConfirmationToken.get());
        verify(mockedConfirmationDao).findByToken(eq(confirmationToken.getToken()));
    }

    @Test
    @DisplayName("findByUserId - token is not found")
    void findByUserIdNonExistent() {
        //prepare
        long nonexistentUserId = 2L;
        when(mockedConfirmationDao.findByUserId(nonexistentUserId)).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> confirmationService.findByUserId(nonexistentUserId));
        //then
        assertEquals(EmailConfirmationErrors.TOKEN_FOR_USER_ID_NOT_FOUND.getMessage(), exception.getMessage());
        verify(mockedConfirmationDao).findByUserId(nonexistentUserId);
    }

    @Test
    @DisplayName("findByUserId - token is found")
    void findByUserId() {
        //prepare
        long userId = 1L;
        ConfirmationToken confirmationToken = dataGenerator.getConfirmationTokenWithUserId(userId);
        Optional<ConfirmationToken> expectedOptional = Optional.of(confirmationToken);
        when(mockedConfirmationDao.findByUserId(userId)).thenReturn(Optional.of(confirmationToken));
        //when
        Optional<ConfirmationToken> actualOptional = confirmationService.findByUserId(userId);
        //then
        assertEquals(expectedOptional, actualOptional);
        verify(mockedConfirmationDao).findByUserId(userId);
    }

    @Test
    @DisplayName("update - confirmation token isn't null")
    void update() {
        //prepare
        ConfirmationToken confirmationToken = dataGenerator.getConfirmationTokenWithUserId(1);
        //when
        confirmationService.update(confirmationToken);
        //then
        verify(mockedConfirmationDao).update(confirmationToken);
    }

    @Test
    @DisplayName("update - confirmation token is null")
    void updateNullConfirmationToken() {
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> confirmationService.update(null));
        //then
        assertEquals(EmailConfirmationErrors.CAN_NOT_CREATE_LINK_TRY_AGAIN.getMessage(), exception.getMessage());
        verify(mockedConfirmationDao, never()).update(null);
    }

    @Test
    @DisplayName("sendMessageWithLinkToUserEmail - email is null")
    void sendMessageWithLinkToUserEmailNullEmail() {
        //prepare
        String tokenAsString = UUID.randomUUID().toString();
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> confirmationService.sendMessageWithLinkToUserEmail(null, tokenAsString));
        //then
        assertEquals(EmailConfirmationErrors.EMPTY_EMAIL.getMessage(), exception.getMessage());
        verify(mockedMailSender, never()).sendMail(eq(null), anyString(), anyString());
    }

    @Test
    @DisplayName("sendMessageWithLinkToUserEmail - token as string is null")
    void sendMessageWithLinkToUserEmailNullToken() {
        //prepare
        String email = dataGenerator.getUserWithNumber(1).getEmail();
        //when
        Exception exception = assertThrows(RuntimeException.class, () -> confirmationService.sendMessageWithLinkToUserEmail(email, null));
        //then
        assertEquals(EmailConfirmationErrors.CAN_NOT_CREATE_LINK_TRY_AGAIN.getMessage(), exception.getMessage());
        verify(mockedMailSender, never()).sendMail(eq(email), anyString(), anyString());
    }

    @Test
    @DisplayName("sendMessageWithLinkToUserEmail - email and confirmation token aren't null")
    void sendMessageWithLinkToUserEmail() {
        //prepare
        String tokenAsString = UUID.randomUUID().toString();
        String email = dataGenerator.getUserWithNumber(1).getEmail();
        //when
        confirmationService.sendMessageWithLinkToUserEmail(email, tokenAsString);
        //then
        verify(mockedMailSender).sendMail(eq(email), anyString(), anyString());
    }

}
