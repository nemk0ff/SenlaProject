package ru.senla.socialnetwork.dto.comments;

public record UpdateCommentDTO(
    Long commentId,
    String body,
    Long authorId) {
}
