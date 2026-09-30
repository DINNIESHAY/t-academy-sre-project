package ru.itis.validation.html;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HtmlEscapeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HtmlEscape {
    String message() default "Текст содержит недопустимые HTML-теги или скрипты";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
