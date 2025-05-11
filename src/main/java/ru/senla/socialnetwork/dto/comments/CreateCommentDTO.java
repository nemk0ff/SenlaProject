package ru.senla.socialnetwork.dto.comments;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentDTO(
    @NotBlank(message = "Нельзя создать пустой комментарий")
    String body) {
}
