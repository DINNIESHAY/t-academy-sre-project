package ru.itis.storage;

import java.io.InputStream;
import java.util.Optional;

public interface FileStorage {

    void save(String key, InputStream content, long size, String contentType);

    Optional<StoredFile> load(String key);
}
