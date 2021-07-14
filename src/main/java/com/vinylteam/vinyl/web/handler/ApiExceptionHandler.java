package com.vinylteam.vinyl.web.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {java.lang.RuntimeException.class})
    public ResponseEntity<Object> handleApiExceptionHandler(java.lang.RuntimeException exception) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(apiExceptionDto);
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
