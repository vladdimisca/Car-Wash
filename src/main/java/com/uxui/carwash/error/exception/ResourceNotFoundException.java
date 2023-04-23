package com.uxui.carwash.error.exception;

import com.uxui.carwash.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AbstractApiException {
    public ResourceNotFoundException(ErrorMessage errorMessage, Object... params) {
        super(HttpStatus.NOT_FOUND, errorMessage, params);
    }
}
