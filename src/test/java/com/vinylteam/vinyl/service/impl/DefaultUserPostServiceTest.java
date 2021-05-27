package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcUserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.util.MailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultUserPostServiceTest {

    private UserPostDao userPostDao = mock(JdbcUserPostDao.class);
    private final MailSender mailSender = mock(MailSender.class);
    DefaultUserPostService userPostService = new DefaultUserPostService(userPostDao, mailSender);

    @Test
    @DisplayName("Checks that when user post saved successfully we receive true from add method")
    void successfullyAddTest() {
        //prepare
        when(userPostDao.add(any())).thenReturn(true);
        //when
        boolean result = userPostService.add(new UserPost("name", "email", "theme", "message", LocalDateTime.now()));
        //then
        assertSame(true, result);
    }

    @Test
    @DisplayName("Checks that when user post saving failed we receive false from add method")
    void failedAddingTest() {
        //prepare
        when(userPostDao.add(any())).thenReturn(false);
        //when
        boolean result = userPostService.add(new UserPost("name", "email", "theme", "message", LocalDateTime.now()));
        //then
        assertSame(false, result);
    }

    @Test
    @DisplayName("Checks that when user post saving successfully and mail sending failed we will receive false from process method")
    void failedMailingSuccessSavingProcessAddTest() {
        //prepare
        when(userPostDao.add(any())).thenReturn(true);
        when(mailSender.sendMail(anyString(), anyString(), anyString())).thenReturn(false);
        //when
        boolean result = userPostService.processAdd(new UserPost("name", "email", "theme", "message", LocalDateTime.now()));
        //then
        assertSame(false, result);
    }

    @Test
    @DisplayName("Checks that when user post saving failed and mail sending failed we will receive false from process method")
    void failedMailingFailedSavingProcessAddTest() {
        //prepare
        when(userPostDao.add(any())).thenReturn(false);
        when(mailSender.sendMail(anyString(), anyString(), anyString())).thenReturn(false);
        //when
        boolean result = userPostService.processAdd(new UserPost("name", "email", "theme", "message", LocalDateTime.now()));
        //then
        assertSame(false, result);
    }

    @Test
    @DisplayName("Checks that when user post saving failed and mail sending success we will receive false from process method")
    void SuccessMailingFailedSavingProcessAddTest() {
        //prepare
        when(userPostDao.add(any())).thenReturn(false);
        when(mailSender.sendMail(anyString(), anyString(), anyString())).thenReturn(true);
        //when
        boolean result = userPostService.processAdd(new UserPost("name", "email", "theme", "message", LocalDateTime.now()));
        //then
        assertSame(false, result);
    }

    @Test
    @DisplayName("Checks that when user post saving failed and mail sending success we will receive false from process method")
    void SuccessMailingSuccessSavingProcessAddTest() {
        //prepare
        when(userPostDao.add(any())).thenReturn(true);
        when(mailSender.sendMail(anyString(), anyString(), anyString())).thenReturn(true);
        //when
        boolean result = userPostService.processAdd(new UserPost("name", "email", "theme", "message", LocalDateTime.now()));
        //then
        assertSame(true, result);
    }

    @Test
    @DisplayName("Checks email message creating contains all words")
    void createContactUsMessageTest() {
        //when
        String contactUsMessage = userPostService.createContactUsMessage("recipient", "subject", "mailBody");
        //then
        assertNotNull((contactUsMessage));
        assertTrue((contactUsMessage.contains("MailFrom:")));
        assertTrue((contactUsMessage.contains("Theme:")));
        assertTrue((contactUsMessage.contains("Message:")));
        assertTrue((contactUsMessage.contains("recipient")));
        assertTrue((contactUsMessage.contains("subject")));
        assertTrue((contactUsMessage.contains("mailBody")));
    }

}