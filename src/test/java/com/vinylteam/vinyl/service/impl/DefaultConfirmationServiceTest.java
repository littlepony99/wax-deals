package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.MailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultConfirmationServiceTest {

    private final ConfirmationTokenDao mockedConfirmationDao = mock(ConfirmationTokenDao.class);
    private final MailSender mockedMailSender = mock(MailSender.class);
    private final ConfirmationService confirmationService = new DefaultConfirmationService(mockedConfirmationDao, mockedMailSender, "localhost:8080");
    private final InOrder inOrderConfirmationDao = Mockito.inOrder(mockedConfirmationDao);
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeEach
    void beforeEach() {
        reset(mockedConfirmationDao);
        reset(mockedMailSender);
    }

    @Test
    @DisplayName("findByToken - token is not correct")
    void findByTokenNotCorrectString() {
        Optional<ConfirmationToken> confirmationToken = confirmationService.findByToken("wrong value");
        assertTrue(confirmationToken.isEmpty());
    }

    @Test
    @DisplayName("findByToken - token is not found")
    void findByTokenNotCorrect() {
        Optional<ConfirmationToken> confirmationToken = confirmationService.findByToken(UUID.randomUUID().toString());
        assertTrue(confirmationToken.isEmpty());
    }

    @Test
    @DisplayName("findByToken - token is found")
    void findByTokenCorrect() {
        UUID tokenUUID = UUID.randomUUID();
        Optional<ConfirmationToken> expectedOptional = Optional.of(new ConfirmationToken());

        when(mockedConfirmationDao.findByToken(tokenUUID)).thenReturn(expectedOptional);
        Optional<ConfirmationToken> confirmationToken = confirmationService.findByToken(tokenUUID.toString());

        assertTrue(confirmationToken.isPresent());
        assertSame(expectedOptional, confirmationToken);
    }

    @Test
    @DisplayName("findByUserId trows IllegalArgumentException and doesn't call dao findByUserId when id <= 0")
    void findByInvalidUserId() {
        //prepare
        long userId = -1L;
        when(mockedConfirmationDao.findByUserId(userId)).thenReturn(Optional.of(new ConfirmationToken()));
        //when
        assertThrows(IllegalArgumentException.class, () -> confirmationService.findByUserId(userId));
        //then
        verify(mockedConfirmationDao, never()).findByUserId(userId);
    }

    @Test
    @DisplayName("findByUserId returns result of dao findByUserId method when id > 0")
    void findByUserId() {
        //prepare
        long userId = 1L;
        Optional<ConfirmationToken> expectedOptional = Optional.of(new ConfirmationToken());
        when(mockedConfirmationDao.findByUserId(userId)).thenReturn(expectedOptional);
        //when
        Optional<ConfirmationToken> actualOptional = confirmationService.findByUserId(userId);
        //then
        assertSame(expectedOptional, actualOptional);
        verify(mockedConfirmationDao).findByUserId(userId);
    }

    @Test
    @DisplayName("addByUserId trows IllegalArgumentException and doesn't call dao add method when id <= 0")
    void addByInvalidUserId() {
        //prepare
        long userId = -1L;
        //when
        assertThrows(IllegalArgumentException.class, () -> confirmationService.addByUserId(userId));
        //then
        verify(mockedConfirmationDao, never()).add(any());
    }

    @Test
    @DisplayName("addByUserId returns result of dao add method when id > 0")
    void addByUserId() {
        //prepare
        long userId = 1L;
        when(mockedConfirmationDao.add(any())).thenReturn(true);
        //when
        ConfirmationToken confirmationToken = confirmationService.addByUserId(userId);
        //then
        assertNotNull(confirmationToken);
        verify(mockedConfirmationDao).add(any());
    }

    @Test
    @DisplayName("update returns result of dao update method when confirmationToken isn't null")
    void update() {
        //prepare
        ConfirmationToken confirmationToken = dataGenerator.getConfirmationTokenWithUserId(1);
        when(mockedConfirmationDao.update(confirmationToken)).thenReturn(true);
        //when
        boolean actualIsUpdated = confirmationService.update(confirmationToken);
        //then
        assertTrue(actualIsUpdated);
        verify(mockedConfirmationDao).update(confirmationToken);
    }

    @Test
    @DisplayName("update throws NullPointerException and doesn't call dao update when confirmationToken is null")
    void updateNullConfirmationToken() {
        //when
        assertThrows(RuntimeException.class, () -> confirmationService.update(null));
        //then
        verify(mockedConfirmationDao, never()).update(null);
    }

    @Test
    @DisplayName("sendMessageWithLinkToUserEmail trows NullPointerException and doesn't call any dao methods or mailSender.sendMessage when user is null")
    void sendMessageWithLinkToUserEmailNullUser() {
        //when
        assertThrows(RuntimeException.class, () -> confirmationService.sendMessageWithLinkToUserEmail(null, UUID.randomUUID().toString()));
    }


    @Test
    @DisplayName("deleteByUserId trows IllegalArgumentException and doesn't call dao deleteByUserId method when id <= 0")
    void deleteByInvalidUserId() {
        //prepare
        long userId = -1L;
        //when
        assertThrows(IllegalArgumentException.class, () -> confirmationService.deleteByUserId(userId));
        //then
        verify(mockedConfirmationDao, never()).deleteByUserId(userId);
    }

    @Test
    @DisplayName("deleteByUserId returns result of dao deleteByUserId method when id > 0")
    void deleteByUserId() {
        //prepare
        long userId = 1L;
        when(mockedConfirmationDao.deleteByUserId(userId)).thenReturn(true);
        //when
        boolean actualIsDeleted = confirmationService.deleteByUserId(userId);
        //then
        assertTrue(actualIsDeleted);
        verify(mockedConfirmationDao).deleteByUserId(userId);
    }

}