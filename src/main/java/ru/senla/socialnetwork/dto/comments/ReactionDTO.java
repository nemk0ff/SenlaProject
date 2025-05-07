package ru.senla.socialnetwork.dto.comments;

import ru.senla.socialnetwork.model.comment.ReactionType;

public record ReactionDTO(
    Long id,
    Long authorId,
    Long commentId,
    ReactionType type) {
}
