package ru.senla.socialnetwork.dto.users;

import java.time.ZonedDateTime;

public record WallPostResponseDTO(
    Long id,
    String wallOwnerEmail,
    String mood,
    String body,
    String location,
    ZonedDateTime createdAt) {
}
