package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CatalogueServletTest {

    private final UniqueVinylService mockedUniqueVinylService = mock(UniqueVinylService.class);
    private final DiscogsService discogsService = mock(DiscogsService.class);
    private final CatalogueServlet catalogueServlet = new CatalogueServlet(mockedUniqueVinylService, discogsService);
    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final InOrder inOrderResponse = inOrder(mockedResponse);
    private final InOrder inOrderRequest = inOrder(mockedRequest);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final User mockedUser = mock(User.class);
    private final PrintWriter mockedPrintWriter = mock(PrintWriter.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
        reset(mockedUser);
        reset(mockedPrintWriter);
    }

    @Test
    @DisplayName("Checks if all right methods are called & session doesn't exist")
    void doGetNoSessionTest() throws IOException {
        //prepare
        when(mockedUniqueVinylService.findManyRandom(50)).thenReturn(
                new ArrayList<>(Collections.nCopies(50, new UniqueVinyl())));
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        catalogueServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedUniqueVinylService).findManyRandom(50);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed")
    void doGetWithAuthedUserAndWantListIsNotEmptyTest() throws IOException {
        //prepare
        when(mockedUniqueVinylService.findManyRandom(50)).thenReturn(
                new ArrayList<>(Collections.nCopies(50, new UniqueVinyl())));
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedRequest.getParameter("wantlist")).thenReturn(null);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        catalogueServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedUniqueVinylService).findManyRandom(50);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        inOrderRequest.verify(mockedRequest).getParameter("wantlist");
        verify(mockedHttpSession).getAttribute("user");
        verify(mockedUser, times(1)).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed, but wantlist is empty. Return random vinyl list")
    void doGetWithAuthedUserAndEmptyWantListLoadCatalogPageTest() throws IOException {
        //prepare
        when(mockedUniqueVinylService.findManyRandom(50)).thenReturn(
                new ArrayList<>(Collections.nCopies(50, new UniqueVinyl())));
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedRequest.getParameter("wantlist")).thenReturn("empty");
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        catalogueServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedUniqueVinylService).findManyRandom(50);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        inOrderRequest.verify(mockedRequest).getParameter("wantlist");
        verify(mockedHttpSession, times(0)).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is not authed")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        when(mockedUniqueVinylService.findManyRandom(50)).thenReturn(
                new ArrayList<>(Collections.nCopies(50, new UniqueVinyl())));
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        catalogueServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedUniqueVinylService).findManyRandom(50);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        verify(mockedUser, times(0)).getRole();
        inOrderResponse.verify(mockedResponse).getWriter();
    }

}