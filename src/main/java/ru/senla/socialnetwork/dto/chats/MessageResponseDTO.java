package ru.senla.socialnetwork.dto.chats;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;

public record MessageResponseDTO(
    Long id,
    Long chatId,
    String body,
    String authorEmail,
    @JsonFormat(shape = JsonFormat.Shape.STRING,
        pattern = "dd-MM-yyyy HH:mm:ss [ZZZ]",
        timezone = "UTC")
    ZonedDateTime createdAt,
    Long replyToId,
    Boolean isPinned
) {}