package ru.senla.socialnetwork.dto.comments;

import ru.senla.socialnetwork.model.comment.ReactionType;

public record ReactionDTO(
    Long id,
    String email,
    Long commentId,
    ReactionType type) {
}