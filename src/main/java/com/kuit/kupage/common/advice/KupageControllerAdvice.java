package com.kuit.kupage.common.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.kuit.kupage.common.response.BaseResponse;
import com.kuit.kupage.exception.AuthException;
import com.kuit.kupage.exception.KupageException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.kuit.kupage.common.response.ResponseCode.BAD_REQUEST;
import static com.kuit.kupage.common.response.ResponseCode.INVALID_INPUT_ENUM;

@Hidden
@RestControllerAdvice
public class KupageControllerAdvice {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> authException(AuthException e) {
        return new ResponseEntity<>(e.getResponseCode(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Object>> handleEnumError(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException) {
            BaseResponse<Object> response = new BaseResponse<>(INVALID_INPUT_ENUM, null);
            return ResponseEntity.badRequest().body(response);
        }
        throw ex;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        BaseResponse<Map<String, String>> response = new BaseResponse<>(BAD_REQUEST, errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(KupageException.class)
    public ResponseEntity<Object> handleException(KupageException e) {
        return new ResponseEntity<>(e.getResponseCode(), HttpStatus.BAD_REQUEST);
    }
}
