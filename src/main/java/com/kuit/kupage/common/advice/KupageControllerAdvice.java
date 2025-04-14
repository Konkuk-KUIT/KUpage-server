package com.kuit.kupage.common.advice;

import com.kuit.kupage.exception.KupageException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class KupageControllerAdvice {

    @ExceptionHandler(KupageException.class)
    public ResponseEntity<Object> handleException(KupageException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
