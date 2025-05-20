package ru.senla.socialnetwork.dto.comments;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о комментарии")
public record CommentDTO(
    @Schema(description = "ID комментария", example = "1")
    Long id,

    @Schema(description = "ID поста", example = "5")
    Long postId,

    @Schema(description = "ID автора", example = "10")
    Long authorId,

    @Schema(description = "Текст комментария", example = "Интересная статья")
    String body
) {
}