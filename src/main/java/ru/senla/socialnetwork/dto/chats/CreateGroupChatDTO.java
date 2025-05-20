package ru.senla.socialnetwork.dto.chats;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import java.util.Set;

@Schema(description = "Данные для создания группового чата")
public record CreateGroupChatDTO(
    @Schema(description = "Название чата", example = "День рождения")
    String name,

    @Schema(description = "Email участников чата",
        example = "[\"user1@senla.ru\", \"user2@senla.ru\"]")
    Set<@Email String> membersEmails
) {
}