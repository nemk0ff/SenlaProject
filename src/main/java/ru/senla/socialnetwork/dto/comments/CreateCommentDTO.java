package ru.senla.socialnetwork.dto.comments;

public record CreateCommentDTO(
    Long postId,
    Long authorId,
    String body) {
}
