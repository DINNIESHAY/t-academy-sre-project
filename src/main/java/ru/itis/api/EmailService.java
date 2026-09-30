package ru.itis.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.api.properties.ResendApiProperties;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class EmailService {

    private final ResendApiProperties apiProperties;
    private final HttpClient httpClient;

    public EmailService(ResendApiProperties apiProperties) {
        this.apiProperties = apiProperties;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendVerificationCode(String targetEmail, String code) {
        String jsonBody = String.format("""
            {
                "from": "%s",
                "to": ["%s"],
                "subject": "Код подтверждения регистрации",
                "html": "<strong>Добро пожаловать! Ваш код: %s</strong>"
            }
            """, apiProperties.getFrom(), targetEmail, code);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiProperties.getApiUrl()))
                    .header("Authorization", "Bearer " + apiProperties.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                log.debug("Письмо успешно отправлено на почту: {}", targetEmail);
            } else {
                log.warn("Ошибка API. Код: {}", response.statusCode());
            }
        } catch (Exception e) {
            log.warn("Ошибка отправки запроса: {}", e.getMessage());
        }
    }
}
