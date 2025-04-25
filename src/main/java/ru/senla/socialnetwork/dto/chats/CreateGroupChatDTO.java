package ru.senla.socialnetwork.dto.chats;

import jakarta.validation.constraints.Email;
import java.util.Set;

public record CreateGroupChatDTO(
    @Email String creatorEmail,
    String name,
    Set<@Email String> membersEmails) {
}
