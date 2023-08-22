package com.whosfritz.breakdecider.Exception;

public class SameVoteAgainException extends RuntimeException {
    public SameVoteAgainException(String message) {
        super(message);
    }
}
