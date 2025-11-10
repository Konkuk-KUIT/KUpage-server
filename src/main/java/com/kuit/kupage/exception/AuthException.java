package com.kuit.kupage.exception;

import com.kuit.kupage.common.response.ResponseCode;

public class AuthException extends KupageException {
  public AuthException(ResponseCode code) {
    super(code);
  }
}
