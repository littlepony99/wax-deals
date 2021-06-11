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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfirmationServletTest {

    private final UserService mockedUserService = mock(DefaultUserService.class);
    private final ConfirmationService mockedConfirmationService = mock(DefaultConfirmationService.class);
    private final ConfirmationServlet confirmationServlet = new ConfirmationServlet(mockedUserService, mockedConfirmationService, 1800);
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
        when(mockedConfirmationService.findByToken(confirmationToken.getToken().toString())).thenReturn(Optional.of(confirmationToken));
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        confirmationServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getParameter("token");
        verify(mockedConfirmationService).findByToken(confirmationToken.getToken().toString());
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    void doGetNonExistentToken() throws IOException {

        //prepare
        String tokenAsString = "wrong value";
        when(mockedRequest.getParameter("token")).thenReturn(tokenAsString);
        when(mockedConfirmationService.findByToken(tokenAsString)).thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        confirmationServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedRequest).getParameter("token");
        verify(mockedConfirmationService).findByToken(tokenAsString);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    void doPostSuccessfulLogIn() throws IOException {
        //prepare
        when(mockedRequest.getParameter("token")).thenReturn(confirmationToken.getToken().toString());
        when(mockedRequest.getParameter("email")).thenReturn(userByLinkToken.getEmail());
        when(mockedRequest.getParameter("password")).thenReturn("rightPassword");
        when(mockedUserService.signInCheck(userByLinkToken.getEmail(), "rightPassword", confirmationToken.getToken().toString())).thenReturn(Optional.of(userByLinkToken));
        when(mockedRequest.getSession(true)).thenReturn(mockedHttpSession);
        //when
        confirmationServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getParameter("token");
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderUserService.verify(mockedUserService).signInCheck(eq(userByLinkToken.getEmail()), eq("rightPassword"), eq(confirmationToken.getToken().toString()));
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).sendRedirect("/");
    }

    @Test
    void doPostFailedLogIn() throws IOException {
        //prepare
        when(mockedRequest.getParameter("token")).thenReturn(confirmationToken.getToken().toString());
        when(mockedRequest.getParameter("email")).thenReturn(userByLinkToken.getEmail());
        when(mockedRequest.getParameter("password")).thenReturn("wrongPassword");
        when(mockedUserService.signInCheck(userByLinkToken.getEmail(), "wrongPassword", confirmationToken.getToken().toString()))
                .thenReturn(Optional.empty());
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        confirmationServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getParameter("token");
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderUserService.verify(mockedUserService).signInCheck(eq(userByLinkToken.getEmail()), eq("wrongPassword"), eq(confirmationToken.getToken().toString()));
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    void doPostWrongEmail() throws IOException {
        //prepare
        String tokenAsString = confirmationToken.getToken().toString();
        when(mockedRequest.getParameter("token")).thenReturn(tokenAsString);
        when(mockedUserService.signInCheck("user2@wax-deals.com", "pp", tokenAsString)).thenReturn(Optional.empty());
        when(mockedRequest.getParameter("email")).thenReturn("user2@wax-deals.com");
        when(mockedRequest.getParameter("password")).thenReturn("rightPassword");
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        confirmationServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderRequest.verify(mockedRequest).getParameter("token");
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");

        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderUserService.verify(mockedUserService).signInCheck(eq("user2@wax-deals.com"), eq("rightPassword"), eq(tokenAsString));
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    void doPostNonExistentToken() throws IOException {
        //prepare
        UUID wrongToken = UUID.randomUUID();
        String wrongTokenString = wrongToken.toString();
        when(mockedRequest.getParameter("token")).thenReturn(wrongTokenString);
        when(mockedRequest.getParameter("email")).thenReturn("user2@wax-deals.com");
        when(mockedRequest.getParameter("password")).thenReturn("rightPassword");
        when(mockedUserService.signInCheck("user2@wax-deals.com", "rightPassword", wrongTokenString))
                .thenThrow(IllegalArgumentException.class);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        assertThrows(IllegalArgumentException.class, () ->
            confirmationServlet.doPost(mockedRequest, mockedResponse)
        );
        //then
        verify(mockedRequest).getParameter("token");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderUserService.verify(mockedUserService).signInCheck(eq("user2@wax-deals.com"), eq("rightPassword"), eq(wrongTokenString));
    }

}