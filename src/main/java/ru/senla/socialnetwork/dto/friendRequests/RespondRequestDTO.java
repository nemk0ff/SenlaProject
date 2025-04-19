package ru.senla.socialnetwork.dto.friendRequests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import ru.senla.socialnetwork.model.enums.FriendStatus;

public record RespondRequestDTO(
    @Email String senderEmail,
    @Email String recipientEmail,
    @NotNull FriendStatus status
) {}
