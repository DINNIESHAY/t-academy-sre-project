package ru.itis.controller.rest;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.service.AvatarService;
import ru.itis.storage.StoredFile;

import java.time.Duration;

@RestController
public class FileRestController {

    private static final CacheControl AVATAR_CACHE_CONTROL = CacheControl.maxAge(Duration.ofDays(30)).cachePrivate();

    private final AvatarService avatarService;

    public FileRestController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("/files/avatars/{fileName:[0-9a-f\\-]+\\.(?:jpg|png|webp)}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String fileName) {
        return avatarService.findAvatar(fileName)
                .map(this::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<Resource> toResponse(StoredFile file) {
        MediaType mediaType = file.contentType() != null
                ? MediaType.parseMediaType(file.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(file.size())
                .cacheControl(AVATAR_CACHE_CONTROL)
                .body(new InputStreamResource(file.content()));
    }
}
