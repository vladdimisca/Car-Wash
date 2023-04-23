package com.uxui.carwash.error.exception;

import com.uxui.carwash.error.ErrorMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

@Getter
public abstract class AbstractApiException extends RuntimeException {
    private final int errorCode;
    private final HttpStatus httpStatus;

    public AbstractApiException(HttpStatus httpStatus, ErrorMessage errorMessage, Object... params) {
        super(formatMessage(errorMessage.getErrorMessage(), params));
        this.errorCode = errorMessage.getErrorCode();
        this.httpStatus = httpStatus;
    }

    public AbstractApiException(HttpStatus httpStatus, String errorMessage) {
        super(errorMessage);
        this.errorCode = -1; // this means that the exception does not have a custom error message
        this.httpStatus = httpStatus;
    }

    private static String formatMessage(String message, Object... messageArgs) {
        return MessageFormat.format(message, messageArgs);
    }
}
