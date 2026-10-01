package ru.itis.emailsender.resend;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.resend")
@Validated
public class ResendProperties {

    @NotEmpty
    private String apiKey;

    @NotEmpty
    @URL
    private String apiUrl;

    @NotEmpty
    private String from;
}
