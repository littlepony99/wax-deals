package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.User;
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

import static org.mockito.Mockito.*;

class DeleteProfileServletTest {

    private final UserService mockedUserService = mock(UserService.class);
    private final DeleteProfileServlet deleteProfileServlet = new DeleteProfileServlet(mockedUserService);

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final User mockedUser = mock(User.class);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final InOrder inOrderRequest = Mockito.inOrder(mockedRequest);
    private final InOrder inOrderResponse = Mockito.inOrder(mockedResponse);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
        reset(mockedUser);
    }

    @Test
    @DisplayName("Redirect to /signIn if session doesn't exist")
    void doPostWhenSessionDoesNotExist() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(null);
        //when
        deleteProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        inOrderResponse.verify(mockedResponse).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("Redirect to /signIn if user doesn't exist")
    void doPostWhenUserDoesNotExist() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        //when
        deleteProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        verify(mockedUserService, times(0)).delete(mockedUser);
        inOrderResponse.verify(mockedResponse).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("Checks doPost method when is trouble with remove user in db")
    void doPostWhenUserDoNotRemoveInDataBase() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUserService.delete(mockedUser)).thenReturn(false);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        deleteProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        verify(mockedUserService, times(1)).delete(mockedUser);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks doPost method when user was deleted from db")
    void doPostWhenUserRemovedFromDataBase() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUserService.delete(mockedUser)).thenReturn(true);
        //when
        deleteProfileServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        verify(mockedUserService, times(1)).delete(mockedUser);
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockedHttpSession).invalidate();
        inOrderResponse.verify(mockedResponse).sendRedirect("/signUp");
    }

}