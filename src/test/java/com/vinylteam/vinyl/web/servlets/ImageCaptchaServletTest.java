package com.vinylteam.vinyl.web.servlets;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;

import static org.mockito.Mockito.*;

class ImageCaptchaServletTest {

    private final ImageCaptchaServlet imageCaptchaServlet = new ImageCaptchaServlet();

    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final HttpSession mockedHttpSession = mock(HttpSession.class);
    private final ServletOutputStream mockedOutputStream = mock(ServletOutputStream.class);
    private final InOrder inOrderRequest = inOrder(mockedRequest);
    private final InOrder inOrderResponse = inOrder(mockedResponse);

    @BeforeEach
    void beforeEach() {
        reset(mockedRequest);
        reset(mockedResponse);
        reset(mockedHttpSession);
    }

    @Test
    @DisplayName("We received right response status + type")
    void doGetWithNotAuthedUserTest() throws IOException {
        //prepare
        when(mockedRequest.getSession()).thenReturn(mockedHttpSession);
        when(mockedHttpSession.getId()).thenReturn("1234567890");
        when(mockedResponse.getOutputStream()).thenReturn(mockedOutputStream);
        //when
        imageCaptchaServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("image/jpeg");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_OK);
        inOrderRequest.verify(mockedRequest).getSession();
    }

}