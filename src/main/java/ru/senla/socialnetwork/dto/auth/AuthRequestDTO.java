package ru.senla.socialnetwork.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequestDTO(
    @Email(message = "Вы ввели неверный формат email")
    @NotBlank(message = "Почта не может быть пустой")
    String email,

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 100)
    String password
) {
}