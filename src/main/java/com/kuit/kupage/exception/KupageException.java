package com.kuit.kupage.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class KupageException extends RuntimeException{

    public KupageException(String message) {
        super(message);
        log.error("{} - message : {}", this.getClass().getSimpleName(), message);
    }
}
