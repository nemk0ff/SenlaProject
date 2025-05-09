package ru.senla.socialnetwork.dto.chats;

import java.time.ZonedDateTime;

public record MessageResponseDTO(
    Long id,
    String body,
    String authorEmail,
    ZonedDateTime createdAt,
    Long replyToId,
    Boolean isPinned
) {}