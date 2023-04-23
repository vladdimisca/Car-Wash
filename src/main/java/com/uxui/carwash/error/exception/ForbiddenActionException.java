package com.uxui.carwash.error.exception;

import com.uxui.carwash.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ForbiddenActionException extends AbstractApiException {
    public ForbiddenActionException(ErrorMessage errorMessage, Object... params) {
        super(HttpStatus.FORBIDDEN, errorMessage, params);
    }
}
