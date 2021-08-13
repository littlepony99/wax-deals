package com.vinylteam.vinyl.web.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

public class ApiExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleApiExceptionHandler(RuntimeException exception) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                "1",
                exception.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(apiExceptionDto);
    }
//FIXME: Fix situation with exceptions in a smart way.
/*    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleUserServiceException(RuntimeException exception) {
        ModelAndView modelAndView = new ModelAndView("editProfile");
        modelAndView.setStatus(BAD_REQUEST);
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }*/

}
