package ru.senla.socialnetwork.dto.communitites;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для создания поста в сообществе")
public record CreateCommunityPostDTO(
    @Schema(description = "Текст поста", example = "Новый пост в сообществе", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank String body,

    @Schema(description = "Закрепить пост", example = "false")
    boolean isPinned
) {
}