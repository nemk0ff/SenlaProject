package ru.senla.socialnetwork.dto.communitites;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для обновления поста в сообществе")
public record UpdateCommunityPostDTO(
    @Schema(description = "Обновленный текст поста", example = "Обновленный текст поста", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank String body,

    @Schema(description = "Закрепить/открепить пост", example = "true")
    boolean isPinned
) {}