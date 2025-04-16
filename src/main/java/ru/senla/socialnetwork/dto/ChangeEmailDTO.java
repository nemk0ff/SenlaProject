package ru.senla.socialnetwork.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailDTO(
    @NotBlank String currentEmail,
    @NotBlank @Email String newEmail) {
}