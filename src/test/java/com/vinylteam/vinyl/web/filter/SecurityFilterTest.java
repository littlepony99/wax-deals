package com.vinylteam.vinyl.web.filter;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;

import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private final SecurityFilter securityFilter = new SecurityFilter();
    private final FilterChain filterChain = mock(FilterChain.class);

    private HttpServletRequest mockedHttpServletRequest;
    private HttpServletResponse mockedHttpServletResponse;
    private HttpSession mockedHttpSession;
    private User mockedUser;
    private InOrder inOrderRequest;
    private InOrder inOrderResponse;
    private Role mockedRole;

    @BeforeEach
    void beforeEach() {
        mockedHttpServletRequest = mock(HttpServletRequest.class);
        mockedHttpServletResponse = mock(HttpServletResponse.class);
        mockedHttpSession = mock(HttpSession.class);
        mockedUser = mock(User.class);
        inOrderRequest = inOrder(mockedHttpServletRequest);
        inOrderResponse = inOrder(mockedHttpServletResponse);
        mockedRole = mock(Role.class);
    }

    @Test
    @DisplayName("When URL is '/'")
    void doFilterWhenUrlIsToHomePageTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL is '/catalog'")
    void doFilterWhenUrlIsToCatalogTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/catalog");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL is '/search'")
    void doFilterWhenUrlIsToSearchTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/search");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL is '/oneVinyl'")
    void doFilterWhenUrlIsToOneVinylTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/oneVinyl");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL is '/signIn'")
    void doFilterWhenUrlIsToSignInTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/signIn");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL is '/signUp'")
    void doFilterWhenUrlIsToSignUpTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/signUp");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL is '/recoveryPassword'")
    void doFilterWhenUrlIsToRecoveryPasswordTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/recoveryPassword");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL is '/css'")
    void doFilterWhenUrlToCssFileTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/css/someFile.css");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL is '/img'")
    void doFilterWhenUrlIsToImageTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/img/someImage.jpg");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL is '/fonts'")
    void doFilterWhenUrlIsToFontsFolderTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/fonts/TimesNewRomance");
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        verify(filterChain).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        verify(mockedHttpServletRequest, times(0)).getSession(false);
    }

    @Test
    @DisplayName("When URL requires authorization & session isn't exist")
    void doFilterWhenUrlRequiresAuthorizationAndSessionIsNotExistTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/requiresAuthorization");
        when(mockedHttpServletRequest.getSession(false)).thenReturn(null);
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        inOrderRequest.verify(mockedHttpServletRequest, times(1)).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("When URL requires authorization & user doesn't auth")
    void doFilterWhenUrlRequiresAuthorizationAndUserDoesNotAuthTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/requiresAuthorization");
        when(mockedHttpServletRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        inOrderRequest.verify(mockedHttpServletRequest, times(1)).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("When URL requires authorization & role of user doesn't match with USER or ADMIN")
    void doFilterWhenUrlRequiresAuthorizationAndUserRoleDoesNotMatchWithUserOrAdminTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/requiresAuthorization");
        when(mockedHttpServletRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn((null));
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        inOrderRequest.verify(mockedHttpServletRequest, times(1)).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        verify(mockedUser, times(1)).getRole();
        verify(filterChain, times(0)).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        inOrderResponse.verify(mockedHttpServletResponse).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("When URL requires authorization & role of user matched with USER")
    void doFilterWhenUrlRequiresAuthorizationAndUserRoleMatchWithUserTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/requiresAuthorization");
        when(mockedHttpServletRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn((mockedRole.USER));
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        inOrderRequest.verify(mockedHttpServletRequest, times(1)).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        verify(mockedUser, times(1)).getRole();
        verify(filterChain, times(1)).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        inOrderResponse.verify(mockedHttpServletResponse, times(0)).sendRedirect("/signIn");
    }

    @Test
    @DisplayName("When URL requires authorization & role of user matched with ADMIN")
    void doFilterWhenUrlRequiresAuthorizationAndUserRoleMatchWithAdminTest() throws IOException, ServletException {
        //prepare
        when(mockedHttpServletRequest.getRequestURI()).thenReturn("/requiresAuthorization");
        when(mockedHttpServletRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn((mockedRole.ADMIN));
        //when
        securityFilter.doFilter(mockedHttpServletRequest, mockedHttpServletResponse, filterChain);
        //then
        inOrderRequest.verify(mockedHttpServletRequest).getRequestURI();
        inOrderRequest.verify(mockedHttpServletRequest, times(1)).getSession(false);
        verify(mockedHttpSession, times(1)).getAttribute("user");
        verify(mockedUser, times(1)).getRole();
        verify(filterChain, times(1)).doFilter(mockedHttpServletRequest, mockedHttpServletResponse);
        inOrderResponse.verify(mockedHttpServletResponse, times(0)).sendRedirect("/signIn");
    }

}