package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.security.SecurityService;
import com.vinylteam.vinyl.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class EditProfileServletTest {

    private final SecurityService mockedSecurityService = mock(SecurityService.class);
    private final UserService mockedUserService = mock(UserService.class);
    private final EditProfileServlet editProfileServlet = new EditProfileServlet(mockedSecurityService, mockedUserService);

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final HttpSession newMockedHttpSession = mock(HttpSession.class);
    private final User mockedUser = mock(User.class);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final InOrder inOrderRequest = Mockito.inOrder(mockedRequest);
    private final InOrder inOrderResponse = Mockito.inOrder(mockedResponse);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
        reset(newMockedHttpSession);
        reset(mockedUser);
    }

    @Test
    @DisplayName("Checks if all right methods are called & session isn't exist")
    void doGetWithNoSessionTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        editProfileServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        verify(mockedUser, times(0)).getEmail();
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is not authed")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        editProfileServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        verify(mockedUser, times(0)).getEmail();
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithAuthedUserTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedUser.getEmail()).thenReturn("authedUser@waxdeals.ua");
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        editProfileServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedUser, times(1)).getEmail();
        assertEquals("authedUser@waxdeals.ua", mockedUser.getEmail());
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks doPost method when HttpSession doesn't exist")
    void doPostWithoutSessionTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("test@email.com");
        when(mockedRequest.getParameter("oldPassword")).thenReturn("oldPassword");
        when(mockedRequest.getParameter("newPassword")).thenReturn("newPassword");
        when(mockedRequest.getParameter("confirmNewPassword")).thenReturn("newPassword");
        when(mockedRequest.getSession(false)).thenReturn(null);
        //when
        editProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("oldPassword");
        inOrderRequest.verify(mockedRequest).getParameter("newPassword");
        inOrderRequest.verify(mockedRequest).getParameter("confirmNewPassword");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        inOrderResponse.verify(mockedResponse).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("Checks doPost method when User doesn't exist into HttpSession")
    void doPostWhenUserDoesNotExistTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("test@email.com");
        when(mockedRequest.getParameter("oldPassword")).thenReturn("oldPassword");
        when(mockedRequest.getParameter("newPassword")).thenReturn("newPassword");
        when(mockedRequest.getParameter("confirmNewPassword")).thenReturn("newPassword");
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        //when
        editProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("oldPassword");
        inOrderRequest.verify(mockedRequest).getParameter("newPassword");
        inOrderRequest.verify(mockedRequest).getParameter("confirmNewPassword");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("Checks doPost method when password & confirmPassword not equal")
    void doPostWhenPasswordNotEqualTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("test@email.com");
        when(mockedRequest.getParameter("oldPassword")).thenReturn("oldPassword");
        when(mockedRequest.getParameter("newPassword")).thenReturn("newPassword");
        when(mockedRequest.getParameter("confirmNewPassword")).thenReturn("confirmNewPassword");
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedUser.getEmail()).thenReturn("test@email.com");
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        editProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("oldPassword");
        inOrderRequest.verify(mockedRequest).getParameter("newPassword");
        inOrderRequest.verify(mockedRequest).getParameter("confirmNewPassword");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedUser, times(1)).getEmail();
        assertEquals("test@email.com", mockedUser.getEmail());
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks doPost method when oldPassword is not correct")
    void doPostWhenOldPasswordIsNotCorrectTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("newTest@email.com");
        when(mockedRequest.getParameter("oldPassword")).thenReturn("oldPassword");
        when(mockedRequest.getParameter("newPassword")).thenReturn("newPassword");
        when(mockedRequest.getParameter("confirmNewPassword")).thenReturn("newPassword");
        when(mockedRequest.getParameter("discogsUserName")).thenReturn("discogsUserName");
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedUser.getEmail()).thenReturn("test@email.com");
        when(mockedSecurityService.checkPasswordAgainstUserPassword(mockedUser, "oldPassword".toCharArray())).thenReturn(false);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        editProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("oldPassword");
        inOrderRequest.verify(mockedRequest).getParameter("newPassword");
        inOrderRequest.verify(mockedRequest).getParameter("confirmNewPassword");
        inOrderRequest.verify(mockedRequest).getParameter("discogsUserName");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedUser, times(1)).getEmail();
        assertEquals("test@email.com", mockedUser.getEmail());
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(mockedUser, "oldPassword".toCharArray());
        verify(mockedUserService, times(0)).update("test@email.com", "newTest@email.com", "newPassword", "discogsUserName");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks doPost method when is trouble with edit user in db")
    void doPostWhenUserDoNotEditInDataBaseTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("newTest@email.com");
        when(mockedRequest.getParameter("oldPassword")).thenReturn("oldPassword");
        when(mockedRequest.getParameter("newPassword")).thenReturn("newPassword");
        when(mockedRequest.getParameter("confirmNewPassword")).thenReturn("newPassword");
        when(mockedRequest.getParameter("discogsUserName")).thenReturn("discogsUserName");
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedUser.getEmail()).thenReturn("test@email.com");
        when(mockedSecurityService.checkPasswordAgainstUserPassword(mockedUser, "oldPassword".toCharArray())).thenReturn(true);
        when(mockedUserService.update("test@email.com", "newTest@email.com", "newPassword", "discogsUserName")).thenReturn(false);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        editProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("oldPassword");
        inOrderRequest.verify(mockedRequest).getParameter("newPassword");
        inOrderRequest.verify(mockedRequest).getParameter("confirmNewPassword");
        inOrderRequest.verify(mockedRequest).getParameter("discogsUserName");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedUser, times(1)).getEmail();
        assertEquals("test@email.com", mockedUser.getEmail());
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(mockedUser, "oldPassword".toCharArray());
        verify(mockedUserService, times(1)).update("test@email.com", "newTest@email.com", "newPassword", "discogsUserName");
        inOrderResponse.verify(mockedResponse, times(0)).setStatus(HttpServletResponse.SC_SEE_OTHER);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks doPost method when everything is good and user was edited & new password isn't empty")
    void doPostWhenUserWasEditedAndNewPasswordIsNotEmptyTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("newTest@email.com");
        when(mockedRequest.getParameter("oldPassword")).thenReturn("oldPassword");
        when(mockedRequest.getParameter("newPassword")).thenReturn("newPassword");
        when(mockedRequest.getParameter("confirmNewPassword")).thenReturn("newPassword");
        when(mockedRequest.getParameter("discogsUserName")).thenReturn("discogsUserName");
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedUser.getEmail()).thenReturn("test@email.com");
        when(mockedSecurityService.checkPasswordAgainstUserPassword(mockedUser, "oldPassword".toCharArray())).thenReturn(true);
        when(mockedUserService.update("test@email.com", "newTest@email.com", "newPassword", "discogsUserName")).thenReturn(true);
        when(mockedRequest.getSession(true)).thenReturn(newMockedHttpSession);
        when(mockedUserService.getByEmail("newTest@email.com")).thenReturn(Optional.ofNullable(mockedUser));
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        editProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("oldPassword");
        inOrderRequest.verify(mockedRequest).getParameter("newPassword");
        inOrderRequest.verify(mockedRequest).getParameter("confirmNewPassword");
        inOrderRequest.verify(mockedRequest).getParameter("discogsUserName");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedUser, times(1)).getEmail();
        assertEquals("test@email.com", mockedUser.getEmail());
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(mockedUser, "oldPassword".toCharArray());
        verify(mockedUserService, times(1)).update("test@email.com", "newTest@email.com", "newPassword", "discogsUserName");
        inOrderResponse.verify(mockedResponse, times(1)).setStatus(HttpServletResponse.SC_SEE_OTHER);
        verify(mockedHttpSession).invalidate();
        inOrderRequest.verify(mockedRequest).getSession(true);
        verify(newMockedHttpSession).setMaxInactiveInterval(60 * 60 * 5);
        verify(mockedUserService).getByEmail("newTest@email.com");
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks doPost method when everything is good and user was edited & new password is empty")
    void doPostWhenUserWasEditedAndNewPasswordIsEmptyTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("newTest@email.com");
        when(mockedRequest.getParameter("oldPassword")).thenReturn("oldPassword");
        when(mockedRequest.getParameter("newPassword")).thenReturn("");
        when(mockedRequest.getParameter("confirmNewPassword")).thenReturn("");
        when(mockedRequest.getParameter("discogsUserName")).thenReturn("discogsUserName");
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedUser.getEmail()).thenReturn("test@email.com");
        when(mockedSecurityService.checkPasswordAgainstUserPassword(mockedUser, "oldPassword".toCharArray())).thenReturn(true);
        when(mockedUserService.update("test@email.com", "newTest@email.com", "oldPassword", "discogsUserName")).thenReturn(true);
        when(mockedRequest.getSession(true)).thenReturn(newMockedHttpSession);
        when(mockedUserService.getByEmail("newTest@email.com")).thenReturn(Optional.ofNullable(mockedUser));
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        editProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("oldPassword");
        inOrderRequest.verify(mockedRequest).getParameter("newPassword");
        inOrderRequest.verify(mockedRequest).getParameter("confirmNewPassword");
        inOrderRequest.verify(mockedRequest).getParameter("discogsUserName");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedUser, times(1)).getEmail();
        assertEquals("test@email.com", mockedUser.getEmail());
        verify(mockedSecurityService).checkPasswordAgainstUserPassword(mockedUser, "oldPassword".toCharArray());
        verify(mockedUserService, times(1)).update("test@email.com", "newTest@email.com", "oldPassword", "discogsUserName");
        inOrderResponse.verify(mockedResponse, times(1)).setStatus(HttpServletResponse.SC_SEE_OTHER);
        verify(mockedHttpSession).invalidate();
        inOrderRequest.verify(mockedRequest).getSession(true);
        verify(newMockedHttpSession).setMaxInactiveInterval(60 * 60 * 5);
        verify(mockedUserService).getByEmail("newTest@email.com");
        inOrderResponse.verify(mockedResponse).getWriter();
    }

}