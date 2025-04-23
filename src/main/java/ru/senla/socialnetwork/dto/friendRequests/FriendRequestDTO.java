package ru.senla.socialnetwork.dto.friendRequests;

import java.time.LocalDateTime;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public record FriendRequestDTO(
    String senderEmail,
    String recipientEmail,
    LocalDateTime createdAt,
    FriendStatus status
) {
}
