package ru.senla.socialnetwork.dto.friendRequests;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public record FriendRequestDTO(
    Long id,
    String senderEmail,
    String recipientEmail,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime createdAt,
    FriendStatus status
) {
}
