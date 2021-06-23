package com.vinylteam.vinyl.web.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleApiExceptionHandler(RuntimeException exception){
        ApiException apiException = new ApiException(
                exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(apiException);
    }

}
