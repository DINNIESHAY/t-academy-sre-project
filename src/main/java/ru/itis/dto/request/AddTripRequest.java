package ru.itis.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.itis.validation.html.HtmlEscape;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class AddTripRequest {

    @NotBlank(message = "Пожалуйста, укажите точку отправления")
    @HtmlEscape
    private String departurePoint;

    @NotBlank(message = "Пожалуйста, укажите точку назначения")
    @HtmlEscape
    private String destination;

    @NotNull(message = "Пожалуйста, укажите дату начала")
    @Future(message = "Дата начала не может быть в прошлом")
    private LocalDate startAt;

    @NotNull(message = "Пожалуйста, укажите дату окончания")
    @Future(message = "Дата начала не может быть в прошлом")
    private LocalDate endAt;

    @NotNull(message = "Пожалуйста, введите бюджет")
    @Positive(message = "Бюджет должен быть больше нуля")
    private BigDecimal budget;

    @NotNull(message = "Пожалуйста, введите количество попутчиков")
    @Min(value = 1, message = "Должен быть минимум 1 попутчик")
    private Integer maxFellows;

    @HtmlEscape
    private String description;

    private List<String> tags;
}
