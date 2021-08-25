package com.vinylteam.vinyl.web.handler;

import com.vinylteam.vinyl.exception.ForbiddenException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeExceptionApiExceptionHandler(RuntimeException exception) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                "1",
                exception.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(apiExceptionDto);
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenExceptionApiExceptionHandler(ForbiddenException exception) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                "1",
                exception.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(apiExceptionDto);
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
