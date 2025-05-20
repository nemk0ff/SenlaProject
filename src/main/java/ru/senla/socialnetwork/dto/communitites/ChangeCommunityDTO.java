package ru.senla.socialnetwork.dto.communitites;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для изменения сообщества")
public record ChangeCommunityDTO(
    @Schema(description = "ID сообщества", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Введите id сообщества")
    Long id,

    @Schema(description = "Новое название сообщества", example = "Новое название", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Введите новое название сообщества")
    String name,

    @Schema(description = "Новое описание сообщества", example = "Обновленное описание")
    String description
) {}