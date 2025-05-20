package ru.senla.socialnetwork.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для создания/обновления поста")
public record WallPostRequestDTO(
    @Schema(description = "Текст поста", example = "Сегодня был прекрасный день!")
    String body,

    @Schema(description = "Настроение автора", example = "happy")
    String mood,

    @Schema(description = "Местоположение", example = "Москва, Россия")
    String location
) {
}
