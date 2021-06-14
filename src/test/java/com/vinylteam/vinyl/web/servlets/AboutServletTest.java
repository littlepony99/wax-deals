
/*
package com.vinylteam.vinyl.web.servlets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class AboutServletTest {

    private final AboutServlet aboutServlet = new AboutServlet();
    private final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
    private final HttpServletResponse mockedResponse = mock(HttpServletResponse.class);
    private final PrintWriter printWriter = new PrintWriter(new StringWriter());
    private final InOrder inOrderResponse = inOrder(mockedResponse);

    @Test
    @DisplayName("Check if GET request returns right content type")
    void doGetReturnsCorrectContentTypeTest() throws IOException {
        //prepare
        when(mockedResponse.getWriter()).thenReturn(printWriter);
        //when
        aboutServlet.doGet(mockedRequest, mockedResponse);
        //then
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
    }

}*/
