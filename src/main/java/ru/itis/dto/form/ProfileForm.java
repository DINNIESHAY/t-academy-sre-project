package ru.itis.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.validation.html.HtmlEscape;

import java.time.LocalDate;

@Data
public class ProfileForm {

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

    @NotBlank(message = "Пожалуйста, укажите Ваш город")
    @Size(max = 100, message = "Введите не более 100 символов")
    @HtmlEscape
    private String city;

    @Size(max = 255, message = "Ссылка изображения слишком длинная")
    private String avatarUrl;

    private MultipartFile avatarFile;

    @HtmlEscape
    private String bio;
}
