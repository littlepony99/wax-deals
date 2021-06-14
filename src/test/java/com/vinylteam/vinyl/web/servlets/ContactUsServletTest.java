
/*
package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UserPostService;
import com.vinylteam.vinyl.service.impl.DefaultCaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class ContactUsServletTest {

    private final UserPostService userPostService = mock(UserPostService.class);
    private final DefaultCaptchaService defaultCaptchaService = mock(DefaultCaptchaService.class);
    private final ContactUsServlet contactUsServlet = new ContactUsServlet(userPostService, defaultCaptchaService);

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final User mockedUser = mock(User.class);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final InOrder inOrderResponse = inOrder(mockedResponse);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
        reset(mockedUser);
    }

    @Test
    @DisplayName("Checks if all right methods are called & session isn't exist")
    void doPostWithNoSessionTest() throws IOException {
        //prepare
        when(mockedRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getId()).thenReturn("sessionId");
        when(mockedRequest.getParameter("captcha")).thenReturn("captcha");
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        when(defaultCaptchaService.validateCaptcha(anyString(), anyString())).thenReturn(true);
        //when
        contactUsServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, never()).getRole();
        verify(mockedUser, never()).getEmail();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has right content type and code set to 200" +
            " and redirected to home page ")
    void doPostWithVerifiedUserRightPasswordTest() throws IOException {
        //prepare
        when(mockedRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getId()).thenReturn("sessionId");
        when(mockedRequest.getParameter("captcha")).thenReturn("captcha");
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        contactUsServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Check if do request returns right content type for user with session")
    void doGetReturnsCorrectContentTypeTest() throws IOException {
        //prepare
        when(mockedRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        contactUsServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
    }

}*/
