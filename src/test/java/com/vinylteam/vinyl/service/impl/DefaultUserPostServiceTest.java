package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.jdbc.JdbcUserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.exception.entity.UserPostErrors;
import com.vinylteam.vinyl.util.MailSender;
import com.vinylteam.vinyl.web.dto.AddUserPostDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
        AddUserPostDto requestDto = AddUserPostDto.builder()
                .email("email.@mail.ru")
                .captchaResponse("captcha")
                .message("message")
                .name("name")
                .build();
        when(captchaService.validateCaptcha(any())).thenReturn(true);
        //when
        userPostService.addUserPostWithCaptchaRequest(requestDto);
    }

    @Test
    @DisplayName("Checks inValid captcha insertion")
    void invalidCaptchaProcessTest() {
        //prepare
        AddUserPostDto requestDto = AddUserPostDto.builder()
                .email("email.@mail.ru")
                .captchaResponse("captcha")
                .message("message")
                .name("name")
                .build();
        when(captchaService.validateCaptcha(any())).thenReturn(false);
        //when
        Exception exception = assertThrows(ForbiddenException.class, () -> userPostService.addUserPostWithCaptchaRequest(requestDto));
        //then
        assertEquals(UserPostErrors.INCORRECT_CAPTCHA_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Checks email message creating contains all words")
    void createContactUsMessageTest() {
        //when
        String contactUsMessage = userPostService.createContactUsMessage("recipient", "mailBody");
        //then
        assertNotNull((contactUsMessage));
        assertTrue((contactUsMessage.contains("MailFrom:")));
        assertTrue((contactUsMessage.contains("Message:")));
        assertTrue((contactUsMessage.contains("recipient")));
        assertTrue((contactUsMessage.contains("mailBody")));
    }
}
