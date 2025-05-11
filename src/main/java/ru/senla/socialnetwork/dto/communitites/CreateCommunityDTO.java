package ru.senla.socialnetwork.dto.communitites;

import jakarta.validation.constraints.NotBlank;

public record CreateCommunityDTO(
    @NotBlank(message = "Введите название сообщества") String name,
    String description) {
}
