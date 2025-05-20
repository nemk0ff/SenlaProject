package ru.senla.socialnetwork.dto.communitites;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.PostDTO;

@Schema(description = "Информация о посте в сообществе")
public record CommunityPostDTO(
    @Schema(description = "ID поста", example = "1")
    Long id,

    @Schema(description = "Email автора", example = "example@senla.ru")
    String authorEmail,

    @Schema(description = "ID сообщества", example = "5")
    Long communityId,

    @Schema(description = "Текст поста", example = "Содержание поста")
    String body,

    @Schema(description = "Дата создания", example = "2025-01-01T12:00:00+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime createdAt,

    @Schema(description = "Закреплен ли пост", example = "false")
    boolean isPinned
) {}