package ru.itis.validation.html;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlEscapeValidator implements ConstraintValidator<HtmlEscape, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        return Jsoup.isValid(value, Safelist.none());
    }
}
