package com.kuit.kupage.common.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.kuit.kupage.exception.KupageException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Hidden
@RestControllerAdvice
public class KupageControllerAdvice {

    @ExceptionHandler(KupageException.class)
    public ResponseEntity<Object> handleException(KupageException e) {
        return new ResponseEntity<>(e.getResponseCode(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleEnumError(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException) {
            Map<String, Object> body = new HashMap<>();
            body.put("code", 400);
            body.put("message", "입력값이 올바르지 않습니다. 허용된 값 중 하나를 사용해야 합니다.");
            body.put("success", false);
            return ResponseEntity.badRequest().body(body);
        }
        throw ex;
    }
}
