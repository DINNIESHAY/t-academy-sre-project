package ru.itis.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.exception.exceptions.AvatarNotSavedException;
import ru.itis.exception.exceptions.InvalidAvatarException;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.path}")
    private String uploadDir;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/webp"
    );

    public String saveAvatar(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return "images/default-avatar.jpg";
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidAvatarException("Некорректный формат файла (поддерживаемые: .jpeg, .png, .webp)");
        }

        try {
            File dir = new File(uploadDir);

            if (!dir.exists() && !dir.mkdirs()) {
                log.warn("Не удалось создать директорию {} для аватара", uploadDir);
                throw new AvatarNotSavedException("Ошибка при сохранении аватара");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFileName = UUID.randomUUID() + extension;
            File destinationFile = new File(uploadDir + uniqueFileName);
            file.transferTo(destinationFile);

            return uniqueFileName;
        } catch (IOException e) {
            log.warn("Не удалось сохранить аватар: {}", e.getMessage());
            throw new AvatarNotSavedException("Ошибка при сохранении аватара");
        }
    }
}