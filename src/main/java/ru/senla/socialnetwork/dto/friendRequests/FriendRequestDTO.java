package ru.senla.socialnetwork.dto.friendRequests;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public record FriendRequestDTO(
    Long id,
    String senderEmail,
    String recipientEmail,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss XXX")
    LocalDateTime createdAt,
    FriendStatus status
) {
}
