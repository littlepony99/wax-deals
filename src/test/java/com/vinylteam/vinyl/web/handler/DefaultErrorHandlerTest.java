package com.vinylteam.vinyl.web.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class DefaultErrorHandlerTest {

    @Test
    @DisplayName("Checks if all right methods are called and response has code set to 400 and mime type" +
            " to \"text/html;charset=utf-8\" when servlet throws an exception and is handled.")
    void generateAcceptableResponseTest() {
        //prepare
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler();
        Request mockedBaseRequest = mock(Request.class);
        HttpServletRequest mockedRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = mock(HttpServletResponse.class);

        when(mockedRequest.getAttribute("javax.servlet.error.servlet_name"))
                .thenReturn("com.vinylteam.vinyl.web.servlets.EditProfileServlet");
        when(mockedRequest.getAttribute("javax.servlet.error.exception"))
                .thenReturn(new NullPointerException());
        InOrder inOrderRequest = Mockito.inOrder(mockedRequest);
        InOrder inOrderResponse = Mockito.inOrder(mockedResponse);
        String message = "NullPointerException";
        String mimeType = "text/html";
        int errorCode = 500;
        //when
        defaultErrorHandler.generateAcceptableResponse(mockedBaseRequest, mockedRequest,
                mockedResponse, errorCode, message, mimeType);
        //then
        inOrderRequest.verify(mockedRequest).getAttribute("javax.servlet.error.servlet_name");
        inOrderRequest.verify(mockedRequest).getAttribute("javax.servlet.error.exception");
        verify(mockedBaseRequest).setHandled(true);
        inOrderResponse.verify(mockedResponse).setContentType("text/html;charset=utf-8");
        inOrderResponse.verify(mockedResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}