package ru.senla.socialnetwork.dto.communitites;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommunityPostDTO(
    @NotBlank String body) {}
