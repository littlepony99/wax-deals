package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.jdbc.JdbcUserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.web.dto.CaptchaRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DefaultUserPostServiceTest {
    @MockBean
    JdbcUserPostDao userPostDao;
    @MockBean
    MailSender mailSender;
    @MockBean
    private DefaultCaptchaService captchaService;
    @Autowired
    private DefaultUserPostService userPostService;

    @Test
    @DisplayName("Checks all necessary methods invocation")
    void rightMethodsInvokedProcessAddTest() {
        //prepare
        UserPost post = mock(UserPost.class);
        //when
        userPostService.processAdd(post);
        //then
        verify(userPostDao, atMostOnce()).add(eq(post));
        verify(post, atMostOnce()).getEmail();
        verify(post, atMostOnce()).getTheme();
        verify(post, atMostOnce()).getMessage();
        verify(mailSender, atMostOnce()).sendMail(eq("recipient"), eq("Mail from customer"), eq("message"));
    }

    @Test
    @DisplayName("Checks VALID captcha insertion")
    void validCaptchaProcessRequestTest() throws ForbiddenException {
        //prepare
        CaptchaRequestDto requestDto = CaptchaRequestDto.builder()
                .email("email.@mail.ru")
                .captchaResponse("captcha")
                .message("message")
                .name("name")
                .subject("subject")
                .build();
        when(captchaService.validateCaptcha(any())).thenReturn(true);
        //when
        boolean result = userPostService.processRequest(requestDto);
        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("Checks inValid captcha insertion")
    void invalidCaptchaProcessTest() {
        //prepare
        CaptchaRequestDto requestDto = CaptchaRequestDto.builder()
                .email("email.@mail.ru")
                .captchaResponse("captcha")
                .message("message")
                .name("name")
                .subject("subject")
                .build();
        when(captchaService.validateCaptcha(any())).thenReturn(false);
        //when
        Exception exception = assertThrows(ForbiddenException.class, () -> userPostService.processRequest(requestDto));
        //then
        assertEquals("INVALID_CAPTCHA", exception.getMessage());
//        verify(userPostService, never()).processAdd(any());
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
