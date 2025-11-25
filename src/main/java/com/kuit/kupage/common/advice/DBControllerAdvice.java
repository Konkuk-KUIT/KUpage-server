package com.kuit.kupage.common.advice;

import com.kuit.kupage.common.response.ResponseCode;
import org.springframework.dao.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DBControllerAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseCode> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return new ResponseEntity<>(ResponseCode.DATA_INTEGRITY_VIOLATION, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<ResponseCode> handleIncorrectResultSize(IncorrectResultSizeDataAccessException e) {
        return new ResponseEntity<>(ResponseCode.INCORRECT_RESULT_SIZE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ResponseCode> handleOptimisticLockFailure(OptimisticLockingFailureException e) {
        return new ResponseEntity<>(ResponseCode.OPTIMISTIC_LOCK_FAILURE, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<ResponseCode> handleQueryTimeout(QueryTimeoutException e) {
        return new ResponseEntity<>(ResponseCode.QUERY_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler({PessimisticLockingFailureException.class, CannotAcquireLockException.class})
    public ResponseEntity<ResponseCode> handlePessimisticLockFailure(RuntimeException e) {
        return new ResponseEntity<>(ResponseCode.PESSIMISTIC_LOCK_FAILURE, HttpStatus.CONFLICT);
    }
}
