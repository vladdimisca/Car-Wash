package com.uxui.carwash.error;

import com.uxui.carwash.error.exception.AbstractApiException;
import com.uxui.carwash.error.exception.BadRequestException;
import com.uxui.carwash.error.exception.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(AbstractApiException.class)
    public ModelAndView handleApiException(AbstractApiException exception) {
        log.info("Custom exception handled: " + exception.getMessage(), exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("exception", exception);
        modelAndView.setViewName("custom-error");
        return modelAndView;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = String.format("The '%s' has an invalid value='%s'.", exception.getName(), exception.getValue());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("exception", new BadRequestException(message));
        modelAndView.setViewName("custom-error");
        return modelAndView;
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView handleDefaultException(Throwable exception) {
        log.error(exception.getMessage(), exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModel().put("exception", new InternalServerErrorException(ErrorMessage.INTERNAL_SERVER_ERROR));
        modelAndView.setViewName("custom-error");
        return modelAndView;
    }
}
