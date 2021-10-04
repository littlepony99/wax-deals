package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.web.handler.ApiExceptionDto;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
@RestController
public class ExceptionHandlingController implements ErrorController {

    @RequestMapping("/error")
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object>  handleError(final Exception exception) throws Throwable {
        return ResponseEntity.status(UNAUTHORIZED).body(new ApiExceptionDto("JWT token is expired or invalid"));
    }

}
