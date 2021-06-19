
/*package com.vinylteam.vinyl.web.handler;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DefaultErrorHandler extends ErrorPageErrorHandler {

    protected void generateAcceptableResponse(Request baseRequest, HttpServletRequest request, HttpServletResponse response,
                                              int code, String message, String mimeType) {
        Map<String, Object> parameterMap = new HashMap<>();
        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        log.error("Error in {'servlet':{},\n 'code':{},\n 'message':{}}", servletName, code, message, exception);
        baseRequest.setHandled(true);
        parameterMap.put("code", code);
        parameterMap.put("message", message);
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.debug("Set response status to " +
                "{'status':{}}", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}*/
