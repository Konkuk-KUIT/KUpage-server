package com.kuit.kupage.exception;

import com.kuit.kupage.common.response.ResponseCode;

public class ProjectException extends KupageException{
    public ProjectException(ResponseCode responseCode) {
        super(responseCode);
    }
}
