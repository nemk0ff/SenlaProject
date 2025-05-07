package ru.senla.socialnetwork.dto.users;

import java.time.ZonedDateTime;

public record WallPostResponseDTO(
    Long id,
    Long wall_owner_id,
    String mood,
    String body,
    ZonedDateTime createdAt,
    boolean isPinned) {
}
