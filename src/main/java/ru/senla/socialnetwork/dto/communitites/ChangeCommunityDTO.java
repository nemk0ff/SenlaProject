package ru.senla.socialnetwork.dto.communitites;

import jakarta.validation.constraints.NotBlank;

public record ChangeCommunityDTO(
    @NotBlank(message = "Введите id сообщества") Long id,
    @NotBlank(message = "Введите название сообщества") String name,
    String description) {
}
