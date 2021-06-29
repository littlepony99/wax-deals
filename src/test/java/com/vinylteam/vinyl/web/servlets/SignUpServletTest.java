
/*package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ConfirmationService;
import com.vinylteam.vinyl.service.UserService;
import com.vinylteam.vinyl.service.impl.DefaultConfirmationService;
import com.vinylteam.vinyl.service.impl.DefaultUserService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class SignUpServletTest {

    private final UserService mockedUserService = mock(DefaultUserService.class);
    private final SignUpServlet signUpServlet = new SignUpServlet(mockedUserService);

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final User mockedUser = mock(User.class);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final InOrder inOrderRequest = Mockito.inOrder(mockedRequest);
    private final InOrder inOrderResponse = Mockito.inOrder(mockedResponse);

    @BeforeEach
    void beforeEach() {
        reset(mockedUserService);
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
        reset(mockedUser);
    }

    @Test
    @DisplayName("Checks if all right methods are called & session isn't exist")
    void doGetWithNoSessionTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithAuthedTestTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is not authed")
    void doGetWithNotAuthedTestTest() throws IOException {
        //prepare
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, times(0)).getRole();
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signUp " +
            "when password is empty.")
    void doPostWhenPasswordIsEmptyTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("existinguser@vinyl.com");
        when(mockedRequest.getParameter("password")).thenReturn("");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("");
        when(mockedRequest.getParameter("discogsUserName")).thenReturn("discogsUserName");
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("discogsUserName");
        verify(mockedUserService, times(0))
                .add("existinguser@vinyl.com", "", "discogsUserName");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signUp " +
            "when password is not correct.")
    void doPostWithNotCorrectPasswordTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("existinguser@vinyl.com");
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("confirmPassword");
        when(mockedRequest.getParameter("discogsUserName")).thenReturn("discogsUserName");
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("discogsUserName");
        verify(mockedUserService, times(0))
                .add("existinguser@vinyl.com", "password", "discogsUserName");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and redirected to /signUp " +
            "when email already exist in db.")
    void doPostWithExistingUserTest() throws IOException {
        //prepare
        when(mockedRequest.getParameter("email")).thenReturn("existinguser@vinyl.com");
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("password");
        when(mockedRequest.getParameter("discogsUserName")).thenReturn("discogsUserName");
        when(mockedUserService.add("existinguser@vinyl.com", "password", "discogsUserName")).thenReturn(false);
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("discogsUserName");
        verify(mockedUserService, times(1))
                .add("existinguser@vinyl.com", "password", "discogsUserName");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 303 and redirected to /signIn " +
            "when email did not exist in database before.")
    void doPostWithNewUserTest() throws IOException {
        //prepare
        User newUser = new User();
        newUser.setEmail("newuser@vinyl.com");
        when(mockedRequest.getParameter("email")).thenReturn(newUser.getEmail());
        when(mockedRequest.getParameter("password")).thenReturn("password");
        when(mockedRequest.getParameter("confirmPassword")).thenReturn("password");
        when(mockedRequest.getParameter("discogsUserName")).thenReturn("discogsUserName");
        when(mockedUserService.add(newUser.getEmail(), "password", "discogsUserName")).thenReturn(true);
        when(mockedUserService.findByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        signUpServlet.doPost(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderRequest.verify(mockedRequest).getParameter("email");
        inOrderRequest.verify(mockedRequest).getParameter("password");
        inOrderRequest.verify(mockedRequest).getParameter("confirmPassword");
        inOrderRequest.verify(mockedRequest).getParameter("discogsUserName");
        verify(mockedUserService, times(1))
                .add(newUser.getEmail(), "password", "discogsUserName");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_SEE_OTHER);
        verify(mockedResponse).getWriter();
    }

    @Test
    void setBadRequest() {
        Map<String, String> attributes = new HashMap<>();
        //when
        signUpServlet.setBadRequest(mockedResponse, attributes, "Error message");
        //then
        verify(mockedResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertEquals("Error message", attributes.get("message"));
    }

}*/
