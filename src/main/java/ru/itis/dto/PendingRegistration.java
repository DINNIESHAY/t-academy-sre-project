package ru.itis.dto;

import java.io.Serializable;

public record PendingRegistration(String username, String email, String encodedPassword) implements Serializable {
}
