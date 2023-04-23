package com.uxui.carwash.model.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class AppointmentTimeValidator implements ConstraintValidator<AppointmentTimeConstraint, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext constraintValidatorContext) {
        if (dateTime == null) {
            return false;
        }
        LocalDateTime currentDate = LocalDateTime.now();
        return dateTime.isAfter(currentDate) && dateTime.isBefore(currentDate.plusYears(1));
    }
}
