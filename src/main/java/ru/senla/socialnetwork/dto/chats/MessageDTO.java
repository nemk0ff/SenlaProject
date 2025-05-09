package ru.senla.socialnetwork.dto.chats;

import java.time.ZonedDateTime;

public record MessageDTO(
    Long id,
    String body,
    String authorEmail,
    ZonedDateTime createdAt,
    Long replyToId,
    Boolean isPinned
) {}