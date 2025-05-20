package ru.senla.socialnetwork.dto.communitites;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для создания сообщества")
public record CreateCommunityDTO(
    @Schema(description = "Название сообщества", example = "Мое сообщество", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Введите название сообщества")
    String name,

    @Schema(description = "Описание сообщества", example = "Описание моего сообщества")
    String description
) {
}