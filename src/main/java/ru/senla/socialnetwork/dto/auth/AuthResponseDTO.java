package ru.senla.socialnetwork.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными аутентификации")
public record AuthResponseDTO(
    @Schema(description = "Роль пользователя", example = "USER")
    String role,

    @Schema(description = "JWT токен", example = "jwt.token.for.senla.user")
    String token
) {
}