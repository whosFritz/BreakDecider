package com.whosfritz.breakdecider.Exception;

public class PasswordIncorrectException extends IllegalArgumentException {
    public PasswordIncorrectException(String message) {
        super(message);
    }
}
