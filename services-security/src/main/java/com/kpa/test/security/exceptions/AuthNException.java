package com.kpa.test.security.exceptions;

public class AuthNException extends RuntimeException{
    public AuthNException(String msg) {
        super(msg);
    }

  public AuthNException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
