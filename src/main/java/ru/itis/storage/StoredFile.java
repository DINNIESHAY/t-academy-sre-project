package ru.itis.storage;

import java.io.InputStream;

public record StoredFile(InputStream content, String contentType, long size) {
}
