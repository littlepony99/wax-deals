package com.vinylteam.vinyl.web.handler;

import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.exception.ServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity<Object> handleBadCredentialsExceptionHandler(BadCredentialsException exception) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(apiExceptionDto);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeExceptionApiExceptionHandler(RuntimeException exception) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                exception.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(apiExceptionDto);
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenExceptionApiExceptionHandler(ForbiddenException exception) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(apiExceptionDto);
    }

    @ExceptionHandler(value = {ServerException.class})
    public ResponseEntity<Object> handleServerExceptionApiExceptionHandler(ServerException exception) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                exception.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(apiExceptionDto);
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
