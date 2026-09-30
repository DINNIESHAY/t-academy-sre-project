package ru.itis.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AddApplicationRequest {

    private String message;

    @NotBlank(message = "Введите Ваши контакты, чтобы автор мог с Вами связаться")
    private String contacts;
}
