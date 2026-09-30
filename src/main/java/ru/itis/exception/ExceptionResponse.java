package ru.itis.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class ExceptionResponse {
    private String message;
    private Map<String, String> errors;
}
