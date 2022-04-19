package ru.javawebinar.topjava.to.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target(FIELD)
@Documented
@Constraint(validatedBy = UniqeEmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "Email is busy";//hard to localize

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
