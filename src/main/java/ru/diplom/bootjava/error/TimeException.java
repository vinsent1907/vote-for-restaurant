package ru.diplom.bootjava.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

public class TimeException extends AppException {
    public TimeException(String msg) {
        super(HttpStatus.LOCKED, msg, ErrorAttributeOptions.of(MESSAGE));
    }
}
