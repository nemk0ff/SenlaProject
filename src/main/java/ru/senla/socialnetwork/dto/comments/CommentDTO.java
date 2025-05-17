package ru.senla.socialnetwork.dto.comments;

public record CommentDTO(
    Long id,
    Long postId,
    Long authorId,
    String body) {
}