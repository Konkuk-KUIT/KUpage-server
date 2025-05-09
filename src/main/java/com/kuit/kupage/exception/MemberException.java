package com.kuit.kupage.exception;

import com.kuit.kupage.common.response.ResponseCode;
import lombok.Getter;

@Getter
public class MemberException extends KupageException{

    public MemberException(ResponseCode responseCode) {
        super(responseCode);
    }
}
