package ru.senla.socialnetwork.dto.communitites;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record UpdateCommunityPostDTO(
    @NotBlank String body,
    List<String> tags
) {}
