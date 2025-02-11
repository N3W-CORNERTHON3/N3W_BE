package com.n3w.threedays.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NoRandomMissionFoundException extends RuntimeException {
    public NoRandomMissionFoundException(String message) {
        super(message);
    }
}


