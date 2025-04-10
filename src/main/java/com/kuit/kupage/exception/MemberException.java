package com.kuit.kupage.exception;

import lombok.Getter;

@Getter
public class MemberException extends KupageException{

    public MemberException(String message) {
        super(message);
    }
}
