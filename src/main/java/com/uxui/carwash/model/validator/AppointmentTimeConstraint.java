package com.uxui.carwash.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AppointmentTimeValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AppointmentTimeConstraint {
    String message() default "Start time must be a future date, not later than one year.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
