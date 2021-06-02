package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.impl.DefaultConfirmationService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfirmationServletTest {

    private final UserService mockedUserService = mock(DefaultUserService.class);
    private final ConfirmationService mockedConfirmationService = mock(DefaultConfirmationService.class);
    private final ConfirmationServlet confirmationServlet = new ConfirmationServlet(mockedUserService, mockedConfirmationService);
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final ConfirmationToken confirmationToken = dataGenerator.getConfirmationTokenWithUserId(1);
    private final User userByLinkToken = dataGenerator.getUserWithNumber(1);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final InOrder inOrderRequest = Mockito.inOrder(mockedRequest);
    private final InOrder inOrderResponse = Mockito.inOrder(mockedResponse);
    private final InOrder inOrderUserService = Mockito.inOrder(mockedUserService);
    private final InOrder inOrderConfirmationService = Mockito.inOrder(mockedConfirmationService);

    @BeforeAll
    void beforeAll() {
        userByLinkToken.setId(1L);
    }

    @BeforeEach
    void beforeEach() {
        reset(mockedUserService);
        reset(mockedConfirmationService);
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
    }

    @Test
    void doGetExistingToken() throws IOException {
        //prepare
        when(mockedRequest.getParameter("token")).thenReturn(confirmationToken.getToken().toString());
        when(mockedConfirmationService.findByToken(confirmationToken.getToken())).thenReturn(Optional.of(confirmationToken));
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        confirmationServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getParameter("token");
        verify(mockedConfirmationService).findByToken(confirmationToken.getToken());
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    void doGetNonExistentToken() throws IOException {
        //prepare
        when(mockedRequest.getParameter("token")).thenReturn("wrong value");
        when(mockedConfirmationService.findByToken(eq(null))).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        confirmationServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getParameter("token");
        verify(mockedConfirmationService).findByToken(null);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    void doPostSuccessfulLogIn() throws IOException {
        //prepare
        when(mockedRequest.getParameter("token")).thenReturn(confirmationToken.getToken().toString());
        when(mockedConfirmationService.findByToken(confirmationToken.getToken())).thenReturn(Optional.of(confirmationToken));
        when(mockedUserService.findById(confirmationToken.getId())).thenReturn(Optional.of(userByLinkToken));
        when(mockedRequest.getParameter("email")).thenReturn(userByLinkToken.getEmail());
        when(mockedRequest.getParameter("password")).thenReturn("rightPassword");
        when(mockedUserService.signInCheck(userByLinkToken.getEmail(), "rightPassword")).thenReturn(Optional.of(userByLinkToken));
        when(mockedUserService.update(userByLinkToken.getEmail(), userByLinkToken.getEmail(), "rightPassword", userByLinkToken.getDiscogsUserName())).thenReturn(true);
        when(mockedRequest.getSession(true)).thenReturn(mockedHttpSession);
        when(mockedConfirmationService.deleteByUserId(userByLinkToken.getId())).thenReturn(true);
        //when
        confirmationServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getParameter("token");
        verify(mockedConfirmationService).findByToken(confirmationToken.getToken());
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderUserService.verify(mockedUserService).findById(confirmationToken.getUserId());
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderUserService.verify(mockedUserService).signInCheck(eq(userByLinkToken.getEmail()), eq("rightPassword"));
        inOrderUserService.verify(mockedUserService).update(eq(userByLinkToken.getEmail()), eq(userByLinkToken.getEmail()), eq("rightPassword"), eq(userByLinkToken.getDiscogsUserName()));
        inOrderConfirmationService.verify(mockedConfirmationService).deleteByUserId(userByLinkToken.getId());
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).sendRedirect("/");
    }

    @Test
    void doPostFailedLogIn() throws IOException {
        //prepare
        when(mockedRequest.getParameter("token")).thenReturn(confirmationToken.getToken().toString());
        when(mockedConfirmationService.findByToken(confirmationToken.getToken())).thenReturn(Optional.of(confirmationToken));
        when(mockedUserService.findById(confirmationToken.getId())).thenReturn(Optional.of(userByLinkToken));
        when(mockedRequest.getParameter("email")).thenReturn(userByLinkToken.getEmail());
        when(mockedRequest.getParameter("password")).thenReturn("wrongPassword");
        when(mockedUserService.signInCheck(userByLinkToken.getEmail(), "wrongPassword")).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        confirmationServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getParameter("token");
        verify(mockedConfirmationService).findByToken(confirmationToken.getToken());
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderUserService.verify(mockedUserService).findById(confirmationToken.getUserId());
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderUserService.verify(mockedUserService).signInCheck(eq(userByLinkToken.getEmail()), eq("wrongPassword"));
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    void doPostWrongEmail() throws IOException {
        //prepare
        when(mockedRequest.getParameter("token")).thenReturn(confirmationToken.getToken().toString());
        when(mockedConfirmationService.findByToken(confirmationToken.getToken())).thenReturn(Optional.of(confirmationToken));
        when(mockedUserService.findById(confirmationToken.getId())).thenReturn(Optional.of(userByLinkToken));
        when(mockedRequest.getParameter("email")).thenReturn("user2@wax-deals.com");
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        confirmationServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getParameter("token");
        verify(mockedConfirmationService).findByToken(confirmationToken.getToken());
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        verify(mockedUserService).findById(confirmationToken.getUserId());
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest, never()).getParameter("password");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    void doPostNonExistentToken() throws IOException {
        //prepare
        when(mockedRequest.getParameter("token")).thenReturn("wrong value");
        when(mockedConfirmationService.findByToken(eq(null))).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        confirmationServlet.doPost(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getParameter("token");
        verify(mockedConfirmationService).findByToken(null);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

}