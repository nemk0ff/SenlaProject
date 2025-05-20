package ru.senla.socialnetwork.dto.comments;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Данные для создания комментария")
public record CreateCommentDTO(
    @Schema(description = "Текст комментария", example = "Отличный пост!")
    @NotBlank(message = "Нельзя создать пустой комментарий")
    String body
) {
}