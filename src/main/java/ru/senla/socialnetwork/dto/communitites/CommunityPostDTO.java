package ru.senla.socialnetwork.dto.communitites;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.PostDTO;

@Schema(description = "DTO поста в сообществе")
public record CommunityPostDTO(
    @Schema(description = "ID поста", example = "123")
    Long id,

    @Schema(description = "Email автора поста", example = "example@senla.ru")
    String authorEmail,

    @Schema(description = "ID сообщества", example = "456")
    Long communityId,

    @Schema(description = "Текст поста", example = "Приветствуем всех участников сообщества!")
    String body,

    @Schema(description = "Дата и время создания поста",
        example = "2025-05-20T14:30:00+03:00",
        type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime createdAt,

    @Schema(description = "Закреплен ли пост", example = "true")
    boolean isPinned
) implements PostDTO {
}