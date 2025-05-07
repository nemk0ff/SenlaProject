package ru.senla.socialnetwork.dto.users;

public record WallPostRequestDTO(
    String body,
    String mood,
    String location
) {
}
