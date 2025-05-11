package ru.senla.socialnetwork.dto.communitites;

import jakarta.validation.constraints.NotBlank;

public record CreateCommunityPostDTO(
    @NotBlank String body,
    boolean isPinned) {}
