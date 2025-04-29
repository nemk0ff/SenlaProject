package ru.senla.socialnetwork.dto.communitites;

import ru.senla.socialnetwork.model.communities.CommunityType;

public record CommunityDTO(
    Long id,
    String owner,
    String name,
    String description,
    CommunityType type) {
}
