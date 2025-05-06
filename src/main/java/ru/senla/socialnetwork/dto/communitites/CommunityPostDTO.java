package ru.senla.socialnetwork.dto.communitites;

import java.time.ZonedDateTime;

public record CommunityPostDTO(
    Long id,
    Long authorId,
    Long communityId,
    String body,
    ZonedDateTime createdAt,
    boolean isPinned) {
}
