package ru.senla.socialnetwork.dto.friendRequests;

import jakarta.validation.constraints.Email;

public record RemoveFriendRequestDTO(
    @Email String userEmail,
    @Email String  friendEmail
    ) {
}
