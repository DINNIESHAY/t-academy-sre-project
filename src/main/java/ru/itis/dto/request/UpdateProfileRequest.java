package ru.itis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.itis.validation.html.HtmlEscape;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Пожалуйста, укажите Ваше имя")
    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    @HtmlEscape
    private String firstName;

    @NotBlank(message = "Пожалуйста, укажите Вашу фамилию")
    @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
    @HtmlEscape
    private String lastName;

    @NotNull(message = "Пожалуйста, укажите Вашу дату рождения")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @NotBlank(message = "Пожалуйста, укажите Ваши контакты")
    @Size(max = 100, message = "Введите не более 100 символов")
    @HtmlEscape
    private String contact;

    @NotBlank(message = "Пожалуйста, укажите Вашу страну")
    @Size(max = 100, message = "Введите не более 100 символов")
    @HtmlEscape
    private String country;

    @NotBlank(message = "Пожалуста, укажите Ваш город")
    @Size(max = 100, message = "Введите не более 100 символов")
    @HtmlEscape
    private String city;

    @HtmlEscape
    private String bio;
}
