package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.RecoveryToken;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class ChangePasswordServletTest {

    private final RecoveryPasswordService mockedRecoveryPasswordService = mock(RecoveryPasswordService.class);
    private final ChangePasswordServlet changePasswordServlet = new ChangePasswordServlet(mockedRecoveryPasswordService);

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
    @DisplayName("doGet method. Session and token don't exist")
    void doGetWithNoSessionTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("token")).thenReturn(token);
        when(mockedRecoveryPasswordService.getByRecoveryToken(token)).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("token");
        verify(mockedRecoveryPasswordService).getByRecoveryToken(token);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doGet method. User is not authed and token doesn't exist")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedRequest.getParameter("token")).thenReturn(token);
        when(mockedRecoveryPasswordService.getByRecoveryToken(token)).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("token");
        verify(mockedRecoveryPasswordService).getByRecoveryToken(token);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doGet method. User is authed and token doesn't exist")
    void doGetWithAuthedUserTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedRequest.getParameter("token")).thenReturn(token);
        when(mockedRecoveryPasswordService.getByRecoveryToken(token)).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        inOrderRequest.verify(mockedRequest).getParameter("token");
        verify(mockedRecoveryPasswordService).getByRecoveryToken(token);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doGet method. Session doesn't exist and token exist but lifetime has expired")
    void doGetIfTokenExistButLifetimeHasExpiredTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        long userId = 1L;
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(userId);
        recoveryToken.setCreatedAt(Timestamp.from(Instant.now()));
        recoveryToken.setLifeTime(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("token")).thenReturn(token);
        when(mockedRecoveryPasswordService.getByRecoveryToken(token)).thenReturn(Optional.of(recoveryToken));
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        inOrderRequest.verify(mockedRequest).getParameter("token");
        verify(mockedRecoveryPasswordService).getByRecoveryToken(token);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedRecoveryPasswordService).removeRecoveryUserToken(token);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doGet method. Session doesn't exist and token exist with correct lifetime, HttpServletResponse.SC_OK")
    void doGetIfTokenExistAndLifetimeIsCorrectTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        long userId = 1L;
        RecoveryToken recoveryToken = dataGenerator.getRecoveryTokenWithUserId(userId);
        recoveryToken.setCreatedAt(Timestamp.from(Instant.now()));
        recoveryToken.setLifeTime(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("token")).thenReturn(token);
        when(mockedRecoveryPasswordService.getByRecoveryToken(token)).thenReturn(Optional.of(recoveryToken));
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        inOrderRequest.verify(mockedRequest).getParameter("token");
        verify(mockedRecoveryPasswordService).getByRecoveryToken(token);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and new password is null")
    void doPostWithNoSessionAndNewPasswordIsNullTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("password")).thenReturn(null);
        when(mockedRequest.getParameter("confirmPassword")).thenReturn(null);
        when(mockedRequest.getParameter("recoveryToken")).thenReturn(token);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("recoveryToken");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. User is not authed and new password is null")
    void doPostWithNotAuthedUserAndNewPasswordIsNullTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedRequest.getParameter("password")).thenReturn(null);
        when(mockedRequest.getParameter("confirmPassword")).thenReturn(null);
        when(mockedRequest.getParameter("recoveryToken")).thenReturn(token);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("recoveryToken");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. User is authed and new password is null")
    void doPostWithAuthedUserAndNewPasswordIsNullTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedRequest.getParameter("password")).thenReturn(null);
        when(mockedRequest.getParameter("confirmPassword")).thenReturn(null);
        when(mockedRequest.getParameter("recoveryToken")).thenReturn(token);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("recoveryToken");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and new password is empty")
    void doPostWithNoSessionAndEmptyNewPasswordTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("password")).thenReturn("");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("");
        when(mockedRequest.getParameter("recoveryToken")).thenReturn(token);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("recoveryToken");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and new password and confirm password don't matched")
    void doPostWithNoSessionAndNewPasswordAndConfirmPasswordDoNotMatchedTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("confirmPassword");
        when(mockedRequest.getParameter("recoveryToken")).thenReturn(token);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("recoveryToken");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and recovery password is empty")
    void doPostWithNoSessionAndRecoveryPasswordIsEmptyTest() throws IOException {
        //prepare
        String token = "some-recovery-token";
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("password");
        when(mockedRequest.getParameter("recoveryToken")).thenReturn(token);
        when(mockedRecoveryPasswordService.getByRecoveryToken(token)).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("recoveryToken");
        verify(mockedRecoveryPasswordService).getByRecoveryToken(token);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and user is empty")
    void doPostWithNoSessionAndUserIsEmptyTest() throws IOException {
        //prepare
        long userId = 1L;
        String token = "some-recovery-token";
        RecoveryToken recoveryTokenWithUserId = dataGenerator.getRecoveryTokenWithUserId(userId);
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("password");
        when(mockedRequest.getParameter("recoveryToken")).thenReturn(token);
        when(mockedRecoveryPasswordService.getByRecoveryToken(token)).thenReturn(Optional.of(recoveryTokenWithUserId));
        when(mockedRecoveryPasswordService.findById(userId)).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("recoveryToken");
        verify(mockedRecoveryPasswordService).getByRecoveryToken(token);
        verify(mockedRecoveryPasswordService).findById(userId);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and user hasn't been updated")
    void doPostWithNoSessionAndUserHasNotBeenUpdatedTest() throws IOException {
        //prepare
        String email = user.getEmail();
        long userId = 1L;
        String token = "some-recovery-token";
        RecoveryToken recoveryTokenWithUserId = dataGenerator.getRecoveryTokenWithUserId(userId);
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("password");
        when(mockedRequest.getParameter("recoveryToken")).thenReturn(token);
        when(mockedRecoveryPasswordService.getByRecoveryToken(token)).thenReturn(Optional.of(recoveryTokenWithUserId));
        when(mockedRecoveryPasswordService.findById(userId)).thenReturn(Optional.of(user));
        when(mockedRecoveryPasswordService.update(email, email, "password", user.getDiscogsUserName())).thenReturn(false);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("recoveryToken");
        verify(mockedRecoveryPasswordService).getByRecoveryToken(token);
        verify(mockedRecoveryPasswordService).findById(userId);
        verify(mockedRecoveryPasswordService).update(email, email, "password", user.getDiscogsUserName());
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("doPost method. Session doesn't exist and user has been updated")
    void doPostWithNoSessionAndUserHasBeenUpdatedTest() throws IOException {
        //prepare
        String email = user.getEmail();
        long userId = 1L;
        String token = "some-recovery-token";
        RecoveryToken recoveryTokenWithUserId = dataGenerator.getRecoveryTokenWithUserId(userId);
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("password");
        when(mockedRequest.getParameter("recoveryToken")).thenReturn(token);
        when(mockedRecoveryPasswordService.getByRecoveryToken(token)).thenReturn(Optional.of(recoveryTokenWithUserId));
        when(mockedRecoveryPasswordService.findById(userId)).thenReturn(Optional.of(user));
        when(mockedRecoveryPasswordService.update(email, email, "password", user.getDiscogsUserName())).thenReturn(true);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        changePasswordServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("recoveryToken");
        verify(mockedRecoveryPasswordService).getByRecoveryToken(token);
        verify(mockedRecoveryPasswordService).findById(userId);
        verify(mockedRecoveryPasswordService).update(email, email, "password", user.getDiscogsUserName());
        verify(mockedRecoveryPasswordService).removeRecoveryUserToken(eq(token));
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        verify(mockedResponse).getWriter();
    }

}