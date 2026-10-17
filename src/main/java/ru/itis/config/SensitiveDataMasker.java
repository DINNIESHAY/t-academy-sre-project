package ru.itis.config;

import org.springframework.boot.json.JsonWriter;
import org.springframework.boot.logging.structured.StructuredLoggingJsonMembersCustomizer;

import java.util.regex.Pattern;

public class SensitiveDataMasker implements StructuredLoggingJsonMembersCustomizer<Object> {

    private static final String REDACTED = "[REDACTED]";
    private static final Pattern SENSITIVE_KEY =
            Pattern.compile("(?i).*(password|secret|token|api_?key|authorization|cookie|session).*");
    private static final Pattern EMAIL = Pattern.compile("([\\w.+-])[\\w.+-]*@([\\w-]+(\\.[\\w-]+)+)");

    @Override
    public void customize(JsonWriter.Members<Object> members) {
        members.applyingValueProcessor((path, value) -> {
            if (path.name() != null && SENSITIVE_KEY.matcher(path.name()).matches()) {
                return REDACTED;
            }
            if (value instanceof String text) {
                return EMAIL.matcher(text).replaceAll("$1***@$2");
            }
            return value;
        });
    }
}
