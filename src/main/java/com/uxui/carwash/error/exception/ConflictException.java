package com.uxui.carwash.error.exception;

import com.uxui.carwash.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ConflictException extends AbstractApiException {
    public ConflictException(ErrorMessage errorMessage, Object... params) {
        super(HttpStatus.CONFLICT, errorMessage, params);
    }
}
