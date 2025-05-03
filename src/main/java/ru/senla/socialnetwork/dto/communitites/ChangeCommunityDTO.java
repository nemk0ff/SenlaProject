package ru.senla.socialnetwork.dto.communitites;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeCommunityDTO(
    @NotNull(message = "Введите id сообщества") Long id,
    @NotBlank(message = "Введите название сообщества") String name,
    String description) {
}
