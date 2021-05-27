package com.vinylteam.vinyl.web.servlets;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.ShopService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class ShopServletTest {

    private final ShopService mockedShopService = mock(ShopService.class);
    private final ShopServlet shopServlet = new ShopServlet(mockedShopService);

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final User mockedUser = mock(User.class);
    private final PrintWriter mockedPrintWriter = mock(PrintWriter.class);
    private final InOrder inOrderResponse = inOrder(mockedResponse);
    private final InOrder inOrderRequest = inOrder(mockedRequest);
    private final List<Shop> testListOfShops;

    ShopServletTest() {
        testListOfShops = new DataGeneratorForTests().getShopsList();
    }

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
        reset(mockedUser);
    }

    @Test
    @DisplayName("Checks if all right methods are called & session isn't exist in ShopServlet`s get() method")
    void doGetWithNoSessionTest() throws IOException {
        //prepare
        when(mockedShopService.findAll()).thenReturn(testListOfShops);
        when(mockedRequest.getSession(false)).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        shopServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedShopService).findAll();
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession, never()).getAttribute("user");
        verify(mockedUser, never()).getRole();
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is authed in ShopServlet`s get() method")
    void doGetWithAuthedUserTest() throws IOException {
        //prepare
        when(mockedShopService.findAll()).thenReturn(testListOfShops);
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(mockedUser);
        when(mockedUser.getRole()).thenReturn(Role.USER);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        shopServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedShopService).findAll();
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertEquals(mockedUser, mockedHttpSession.getAttribute("user"));
        verify(mockedUser).getRole();
        assertEquals(Role.USER, mockedUser.getRole());
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

    @Test
    @DisplayName("Checks if all right methods are called & user is not authed in ShopServlet`s get() method")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        when(mockedShopService.findAll()).thenReturn(testListOfShops);
        when(mockedRequest.getSession(false)).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getAttribute("user")).thenReturn(null);
        when(mockedResponse.getWriter()).thenReturn(mockedPrintWriter);
        //when
        shopServlet.doGet(mockedRequest, mockedResponse);
        //then
        verify(mockedShopService).findAll();
        inOrderRequest.verify(mockedRequest).getSession(false);
        verify(mockedHttpSession).getAttribute("user");
        assertNull(mockedHttpSession.getAttribute("user"));
        verify(mockedUser, never()).getRole();
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderResponse.verify(mockedResponse).getWriter();
    }

}