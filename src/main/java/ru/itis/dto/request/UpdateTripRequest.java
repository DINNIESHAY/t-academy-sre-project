package ru.itis.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.itis.validation.html.HtmlEscape;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class UpdateTripRequest {

    @Positive(message = "Бюджет должен быть больше нуля")
    private BigDecimal budget;

    @Min(value = 1, message = "Должен быть минимум 1 попутчик")
    private Integer maxFellows;

    @HtmlEscape
    private String description;
}
