package com.whosfritz.breakdecider.Exception;

public class NewEqualsOldPasswordException extends IllegalArgumentException {
    public NewEqualsOldPasswordException(String message) {
        super(message);
    }
}
