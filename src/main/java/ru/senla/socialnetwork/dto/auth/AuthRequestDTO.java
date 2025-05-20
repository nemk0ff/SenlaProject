package ru.senla.socialnetwork.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для аутентификации")
public record AuthRequestDTO(
    @Schema(description = "Email пользователя", example = "example@senla.ru")
    @Email(message = "Вы ввели неверный формат email")
    @NotBlank(message = "Почта не может быть пустой")
    String email,

    @Schema(description = "Пароль", example = "password123", minLength = 8, maxLength = 100)
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 100)
    String password
) {
}