package com.kuit.kupage.common.advice;

import com.kuit.kupage.exception.KupageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class KupageControllerAdvice {

    @ExceptionHandler(KupageException.class)
    public ResponseEntity<Object> handleException(KupageException e) {
        return new ResponseEntity<>(e.getResponseCode(), HttpStatus.BAD_REQUEST);
    }
}
