package ru.senla.socialnetwork.dto.friendRequests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public record RespondRequestDTO(
    @Email String senderEmail,
    @NotNull FriendStatus respondStatus
) {}
