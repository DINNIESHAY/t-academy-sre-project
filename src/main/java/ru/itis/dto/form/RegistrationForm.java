package ru.itis.dto.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.itis.validation.html.HtmlEscape;
import ru.itis.validation.password.PasswordMatches;

@Data
@PasswordMatches
public class RegistrationForm {

    @NotBlank(message = "Пожалуйста, укажите имя пользователя")
    @Size(min = 3, max = 20, message = "Имя должно быть от 3 до 20 символов")
    @HtmlEscape
    private String username;

    @NotBlank(message = "Пожалуйста, укажите Ваш email")
    @Email(message = "Некорректный формат mail")
    @HtmlEscape
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    @HtmlEscape
    private String password;

    @NotBlank(message = "Повторите пароль")
    @HtmlEscape
    private String confirmPassword;
}
