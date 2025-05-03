package ru.senla.socialnetwork.dto.communitites;

public record CommunityDTO(
    Long id,
    String owner,
    String name,
    String description) {
}
