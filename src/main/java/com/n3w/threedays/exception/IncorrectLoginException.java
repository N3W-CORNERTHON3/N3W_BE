package com.n3w.threedays.exception;

public class IncorrectLoginException extends RuntimeException {
    public IncorrectLoginException(String message) {
        super(message);
    }
}
