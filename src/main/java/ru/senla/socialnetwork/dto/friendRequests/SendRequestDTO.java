package ru.senla.socialnetwork.dto.friendRequests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendRequestDTO(
    @Email
    @NotBlank(message = "укажите email отправителя заявки")
    String senderEmail,
    @Email
    @NotBlank(message = "укажите email получателя заявки")
    String recipientEmail) {
}
