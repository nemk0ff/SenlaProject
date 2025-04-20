package ru.senla.socialnetwork.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailDTO(
    @NotBlank @Email String currentEmail,
    @NotBlank @Email String newEmail) {
}