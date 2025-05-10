package com.kuit.kupage.exception;

import com.kuit.kupage.common.response.ResponseCode;

public class ArticleException extends KupageException {
    public ArticleException(ResponseCode responseCode) {
        super(responseCode);
    }
}
