package ru.senla.socialnetwork.dto.chats;

import jakarta.validation.constraints.Email;

public record CreatePersonalChatDTO(
    @Email String creatorEmail,
    @Email String friendEmail) {
}
