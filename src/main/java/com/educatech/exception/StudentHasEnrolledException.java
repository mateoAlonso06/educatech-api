package com.educatech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict is a good status for this
public class StudentHasEnrolledException extends RuntimeException {
    public StudentHasEnrolledException(String message) {
        super(message);
    }
}
