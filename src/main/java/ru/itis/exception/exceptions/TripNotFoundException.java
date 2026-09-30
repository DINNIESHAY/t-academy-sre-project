package ru.itis.exception.exceptions;

import org.jspecify.annotations.Nullable;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(@Nullable String tripId) {
        super("Поездка не найдена (id=" + tripId + ")");
    }
}
