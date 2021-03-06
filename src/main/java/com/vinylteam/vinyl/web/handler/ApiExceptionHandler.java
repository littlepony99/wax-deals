package com.vinylteam.vinyl.web.handler;

import com.vinylteam.vinyl.exception.DiscogsBadRequestException;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.exception.ServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity<Object> handleBadCredentialsExceptionHandler(BadCredentialsException exception) {
        return ResponseEntity.status(BAD_REQUEST).body(new ApiExceptionDto(exception.getMessage()));
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedExceptionApiExceptionHandler(AccessDeniedException exception) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ApiExceptionDto(exception.getMessage()));
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeExceptionApiExceptionHandler(RuntimeException exception) {
        return ResponseEntity.status(FORBIDDEN).body(new ApiExceptionDto(exception.getMessage()));
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenExceptionApiExceptionHandler(ForbiddenException exception) {
        return ResponseEntity.status(BAD_REQUEST).body(new ApiExceptionDto(exception.getMessage()));
    }

    @ExceptionHandler(value = {ServerException.class})
    public ResponseEntity<Object> handleServerExceptionApiExceptionHandler(ServerException exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiExceptionDto(exception.getMessage()));
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundExceptionApiExceptionHandler(NotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiExceptionDto(exception.getMessage()));
    }

    @ExceptionHandler(value = {DiscogsBadRequestException.class})
    public ResponseEntity<Object> handleDiscogsUserNotFoundExceptionHandler(DiscogsBadRequestException exception) {
        return ResponseEntity.status(BAD_REQUEST).body(new ApiExceptionDto(exception.getMessage()));
    }

}
