package ru.senla.socialnetwork.dto.comments;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для обновления комментария")
public record UpdateCommentDTO(
    @Schema(
        description = "Новый текст комментария",
        example = "Обновленный текст комментария",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String body
) {
}