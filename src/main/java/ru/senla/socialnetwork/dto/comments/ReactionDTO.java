package ru.senla.socialnetwork.dto.comments;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.senla.socialnetwork.model.comment.ReactionType;

@Schema(description = "Информация о реакции")
public record ReactionDTO(
    @Schema(description = "ID реакции", example = "1")
    Long id,

    @Schema(description = "Email пользователя", example = "user@example.com")
    String email,

    @Schema(description = "ID комментария", example = "5")
    Long commentId,

    @Schema(description = "Тип реакции", example = "LIKE")
    ReactionType type
) {
}