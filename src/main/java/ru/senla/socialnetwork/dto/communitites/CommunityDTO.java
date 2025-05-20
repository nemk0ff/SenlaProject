package ru.senla.socialnetwork.dto.communitites;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о сообществе")
public record CommunityDTO(
    @Schema(description = "ID сообщества", example = "1")
    Long id,

    @Schema(description = "Название сообщества", example = "Мое сообщество")
    String name,

    @Schema(description = "Описание сообщества", example = "Описание моего сообщества")
    String description
) {}