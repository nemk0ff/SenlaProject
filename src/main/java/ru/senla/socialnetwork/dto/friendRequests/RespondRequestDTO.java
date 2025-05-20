package ru.senla.socialnetwork.dto.friendRequests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@Schema(description = "Запрос на обработку заявки в друзья")
public record RespondRequestDTO(
    @Schema(description = "Email отправителя заявки", example = "example@senla.ru")
    @Email String senderEmail,

    @Schema(description = "Новый статус заявки")
    @NotNull FriendStatus respondStatus
) {
}