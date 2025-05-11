package ru.senla.socialnetwork.dto.chats;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;

public record MessageResponseDTO(
    Long id,
    Long chatId,
    String body,
    String authorEmail,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime createdAt,
    Long replyToId,
    Boolean isPinned
) {}