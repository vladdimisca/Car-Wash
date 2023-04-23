package com.uxui.carwash.error.exception;

import com.uxui.carwash.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractApiException {
    public BadRequestException(ErrorMessage errorMessage, Object... params) {
        super(HttpStatus.BAD_REQUEST, errorMessage, params);
    }

    public BadRequestException(String errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage);
    }
}
