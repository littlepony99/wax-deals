package com.vinylteam.vinyl.web.handler;

import com.vinylteam.vinyl.exception.UserServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleApiExceptionHandler(RuntimeException exception) {
        ApiExceptionDto apiExceptionDto = new ApiExceptionDto(
                exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(apiExceptionDto);
    }

    @ExceptionHandler(UserServiceException.class)
    public ModelAndView handleUserServiceException(UserServiceException exception) {
        ModelAndView modelAndView = new ModelAndView("editProfile");
        modelAndView.setStatus(BAD_REQUEST);
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }

}
