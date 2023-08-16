package com.whosfritz.breakdecider.Exception;

public class InvalidTokenException extends IllegalArgumentException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
