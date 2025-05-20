package ru.senla.socialnetwork.dto.friendRequests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@Schema(description = "DTO заявки в друзья")
public record FriendRequestDTO(
    @Schema(description = "ID заявки", example = "1")
    Long id,

    @Schema(description = "Email отправителя", example = "sender@senla.ru")
    String senderEmail,

    @Schema(description = "Email получателя", example = "recipient@senla.ru")
    String recipientEmail,

    @Schema(description = "Дата создания заявки", example = "2023-05-20T14:30:00+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime createdAt,

    @Schema(description = "Статус заявки")
    FriendStatus status
) {
}