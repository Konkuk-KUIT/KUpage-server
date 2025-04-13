package com.kuit.kupage.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class KupageException extends RuntimeException{

    private static final String APP_PACKAGE_PREFIX = "com.kuit.kupage";

    public KupageException(String message) {
        super(message);
        StackTraceElement[] stackTrace = getStackTrace();

        StringBuilder filtered = new StringBuilder();

        filtered.append(this.getClass().getSimpleName())
                .append(" - message : ")
                .append(message)
                .append("\n");

        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith(APP_PACKAGE_PREFIX)) {
                filtered.append("\tat ").append(element).append("\n");
            }
        }

        log.warn(filtered.toString());
    }
}
