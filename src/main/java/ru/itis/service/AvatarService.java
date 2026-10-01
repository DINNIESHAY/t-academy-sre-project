package ru.itis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.exception.exceptions.AvatarNotSavedException;
import ru.itis.exception.exceptions.InvalidAvatarException;
import ru.itis.storage.FileStorage;
import ru.itis.storage.FileStorageException;
import ru.itis.storage.StoredFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AvatarService {

    private static final String AVATARS_KEY_PREFIX = "avatars/";
    private static final String AVATARS_URL_PREFIX = "files/" + AVATARS_KEY_PREFIX;

    private static final String DEFAULT_AVATAR_URL = "images/default-avatar.jpg";

    private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final FileStorage fileStorage;

    public AvatarService(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public String saveAvatar(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return DEFAULT_AVATAR_URL;
        }

        String contentType = file.getContentType();
        String extension = contentType == null ? null : EXTENSIONS_BY_CONTENT_TYPE.get(contentType);
        if (extension == null) {
            throw new InvalidAvatarException("Некорректный формат файла (поддерживаемые: .jpeg, .png, .webp)");
        }

        String fileName = UUID.randomUUID() + extension;

        try (InputStream content = file.getInputStream()) {
            fileStorage.save(AVATARS_KEY_PREFIX + fileName, content, file.getSize(), contentType);
        } catch (IOException | FileStorageException e) {
            log.warn("Не удалось сохранить аватар", e);
            throw new AvatarNotSavedException("Ошибка при сохранении аватара");
        }

        return AVATARS_URL_PREFIX + fileName;
    }

    public Optional<StoredFile> findAvatar(String fileName) {
        return fileStorage.load(AVATARS_KEY_PREFIX + fileName);
    }
}
