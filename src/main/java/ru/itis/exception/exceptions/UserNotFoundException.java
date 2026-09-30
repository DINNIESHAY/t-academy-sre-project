package ru.itis.exception.exceptions;

import org.jspecify.annotations.Nullable;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(@Nullable String username) {
        super("Пользователь не найден: " + username);
    }
}
