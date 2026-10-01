package ru.itis.storage.s3;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.storage.s3")
@Validated
public class S3Properties {

    private String endpoint;

    @NotEmpty
    private String region;

    @NotEmpty
    private String bucket;

    @NotEmpty
    private String accessKey;

    @NotEmpty
    private String secretKey;

    private boolean pathStyleAccess;
}
