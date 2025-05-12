package ru.senla.socialnetwork.dto.chats;

import jakarta.validation.constraints.NotBlank;

public record MessageRequestDTO(
    @NotBlank(message = "Укажите тело сообщения") String body,
    Long replyToId
) {
}
