
/*
package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.RecoveryPasswordException;
import com.vinylteam.vinyl.exception.entity.ErrorRecoveryPassword;
import com.vinylteam.vinyl.service.RecoveryPasswordService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class RecoveryPasswordServletTest {
    private final RecoveryPasswordService mockedRecoveryPasswordService = mock(RecoveryPasswordService.class);
    private final RecoveryPasswordServlet recoveryPasswordServlet = new RecoveryPasswordServlet(mockedRecoveryPasswordService);

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final User mockedUser = mock(User.class);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final InOrder inOrderResponse = inOrder(mockedResponse);
    private final InOrder inOrderRequest = inOrder(mockedRequest);
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final List<User> usersList = dataGenerator.getUsersList();
    private final User user = usersList.get(0);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
        reset(mockedUser);
    }

    @Test
    @DisplayName("doGet method. Session doesn't exist")
    void doGetWithNoSessionTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        recoveryPasswordServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doGet method. User is not authed")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        recoveryPasswordServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doGet method. User is authed")
    void doGetWithAuthedUserTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        recoveryPasswordServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and email is null")
    void doPostWithNoSessionAndEmailIsNullTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn(null);
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        doThrow(new RecoveryPasswordException(ErrorRecoveryPassword.EMPTY_EMAIL.getMessage()))
                .when(mockedRecoveryPasswordService).sendLink(null);
        //when
        recoveryPasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedRecoveryPasswordService).sendLink(null);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. User is not authed and email is null")
    void doPostWithNotAuthedUserAndEmailIsNullTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn(null);
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        doThrow(new RecoveryPasswordException(ErrorRecoveryPassword.EMPTY_EMAIL.getMessage()))
                .when(mockedRecoveryPasswordService).sendLink(null);
        //when
        recoveryPasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        verify(mockedRecoveryPasswordService).sendLink(null);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. User is not authed and email is null")
    void doPostWithAuthedUserAndEmailIsNullTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn(null);
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        doThrow(new RecoveryPasswordException(ErrorRecoveryPassword.EMPTY_EMAIL.getMessage()))
                .when(mockedRecoveryPasswordService).sendLink(null);
        //when
        recoveryPasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedRecoveryPasswordService).sendLink(null);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and user by email doesn't exist")
    void doPostWithNoSessionAndUserDoesNotExistTest() throws IOException {
        //prepare
        String email = user.getEmail();
        when(mockedRequest.getParameter("email")).thenReturn(email);
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        doThrow(new RecoveryPasswordException(ErrorRecoveryPassword.EMAIL_NOT_FOUND_IN_DB.getMessage()))
                .when(mockedRecoveryPasswordService).sendLink(email);
        //when
        recoveryPasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedRecoveryPasswordService).sendLink(email);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and error during adding token")
    void doPostWithNoSessionAndRecoveryTokenIsEmptyTest() throws IOException {
        //prepare
        long userId = 1L;
        String email = user.getEmail();
        user.setId(userId);
        when(mockedRequest.getParameter("email")).thenReturn(email);
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        doThrow(new RecoveryPasswordException(ErrorRecoveryPassword.ADD_TOKEN_ERROR.getMessage()))
                .when(mockedRecoveryPasswordService).sendLink(email);

        //when
        recoveryPasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedRecoveryPasswordService).sendLink(email);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and mail doesn't send")
    void doPostWithNoSessionAndMailDoesNotSendTest() throws IOException {
        //prepare
        long userId = 1L;
        String email = user.getEmail();
        user.setId(userId);
        when(mockedRequest.getParameter("email")).thenReturn(email);
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        doThrow(new RecoveryPasswordException(ErrorRecoveryPassword.EMAIL_SEND_ERROR.getMessage()))
                .when(mockedRecoveryPasswordService).sendLink(email);

        //when
        recoveryPasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedRecoveryPasswordService).sendLink(email);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and mail was sent")
    void doPostWithNoSessionAndMailWasSentTest() throws IOException {
        //prepare
        long userId = 1L;
        String email = user.getEmail();
        user.setId(userId);
        when(mockedRequest.getParameter("email")).thenReturn(email);
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        recoveryPasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedRecoveryPasswordService).sendLink(email);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockedResponse).getWriter();
    }

}*/
