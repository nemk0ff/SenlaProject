package ru.senla.socialnetwork.dto.comments;

public record UpdateCommentDTO(
    Long commentId,
    Long authorId,
    String body) {
}
