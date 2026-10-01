package ru.itis.storage.s3;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.itis.storage.FileStorage;
import ru.itis.storage.FileStorageException;
import ru.itis.storage.StoredFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;
import java.util.Optional;

@Component
@Slf4j
public class S3FileStorage implements FileStorage {

    private static final int NOT_FOUND = 404;
    private static final int CONFLICT = 409;

    private final S3Client s3Client;
    private final String bucket;

    public S3FileStorage(S3Client s3Client, S3Properties properties) {
        this.s3Client = s3Client;
        this.bucket = properties.getBucket();
    }

    @PostConstruct
    void ensureBucketExists() {
        try {
            s3Client.headBucket(request -> request.bucket(bucket));
        } catch (S3Exception e) {
            if (e.statusCode() != NOT_FOUND) {
                throw e;
            }
            createBucket();
        }
    }

    private void createBucket() {
        try {
            s3Client.createBucket(request -> request.bucket(bucket));
            log.info("Создан бакет {}", bucket);
        } catch (S3Exception e) {
            if (e.statusCode() != CONFLICT) {
                throw e;
            }
            log.info("Бакет {} уже создан другим экземпляром приложения", bucket);
        }
    }

    @Override
    public void save(String key, InputStream content, long size, String contentType) {
        try {
            s3Client.putObject(
                    request -> request.bucket(bucket).key(key).contentType(contentType),
                    RequestBody.fromInputStream(content, size)
            );
        } catch (SdkException e) {
            throw new FileStorageException("Не удалось сохранить файл " + key, e);
        }
    }

    @Override
    public Optional<StoredFile> load(String key) {
        try {
            ResponseInputStream<GetObjectResponse> object =
                    s3Client.getObject(request -> request.bucket(bucket).key(key));
            GetObjectResponse response = object.response();
            return Optional.of(new StoredFile(object, response.contentType(), response.contentLength()));
        } catch (S3Exception e) {
            if (e.statusCode() == NOT_FOUND) {
                return Optional.empty();
            }
            throw new FileStorageException("Не удалось загрузить файл " + key, e);
        } catch (SdkException e) {
            throw new FileStorageException("Не удалось загрузить файл " + key, e);
        }
    }
}
