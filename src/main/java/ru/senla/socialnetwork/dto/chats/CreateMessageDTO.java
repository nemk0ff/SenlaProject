package ru.senla.socialnetwork.dto.chats;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateMessageDTO(
    @Email String senderEmail,
    @NotBlank(message = "Укажите тело сообщения") String body,
    Long replyToId
) {}
