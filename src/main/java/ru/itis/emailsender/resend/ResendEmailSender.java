package ru.itis.emailsender.resend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.emailsender.EmailSender;
import ru.itis.metrics.BusinessMetrics;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@Slf4j
public class ResendEmailSender implements EmailSender {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final ResendProperties apiProperties;
    private final HttpClient httpClient;
    private final BusinessMetrics metrics;

    public ResendEmailSender(ResendProperties apiProperties, BusinessMetrics metrics) {
        this.apiProperties = apiProperties;
        this.metrics = metrics;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();
    }

    @Override
    public void sendVerificationCode(String targetEmail, String code) {
        String jsonBody = String.format("""
            {
                "from": "%s",
                "to": ["%s"],
                "subject": "Код подтверждения регистрации",
                "html": "<strong>Добро пожаловать! Ваш код: %s</strong>"
            }
            """, apiProperties.getFrom(), targetEmail, code);

        long start = System.nanoTime();
        boolean sent = false;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiProperties.getApiUrl()))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Authorization", "Bearer " + apiProperties.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                sent = true;
                log.debug("Verification email sent");
            } else {
                log.atWarn().addKeyValue("http.response.status_code", response.statusCode()).log("Email provider rejected request");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Email sending interrupted");
        } catch (Exception e) {
            log.warn("Failed to send email", e);
        } finally {
            metrics.verificationEmailSent(sent, Duration.ofNanos(System.nanoTime() - start));
        }
    }
}
