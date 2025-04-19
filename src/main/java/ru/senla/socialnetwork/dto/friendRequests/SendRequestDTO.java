package ru.senla.socialnetwork.dto.friendRequests;

import jakarta.validation.constraints.Email;

public record SendRequestDTO(
    @Email String senderEmail,
    @Email String recipientEmail) {
}
