package ru.senla.socialnetwork.dto.comments;

import ru.senla.socialnetwork.model.comment.ReactionType;

public record CreateReactionDTO(
    Long commentId,
    ReactionType type) {
}
