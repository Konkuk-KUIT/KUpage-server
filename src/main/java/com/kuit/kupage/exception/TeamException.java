package com.kuit.kupage.exception;

import com.kuit.kupage.common.response.ResponseCode;

public class TeamException extends KupageException {
    public TeamException(ResponseCode responseCode) {
        super(responseCode);
    }
}
