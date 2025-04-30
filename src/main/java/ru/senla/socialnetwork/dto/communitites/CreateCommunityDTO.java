package ru.senla.socialnetwork.dto.communitites;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCommunityDTO(
    @Email @NotBlank String owner,
    @NotBlank(message = "Введите название сообщества") String name,
    String description) {
}
