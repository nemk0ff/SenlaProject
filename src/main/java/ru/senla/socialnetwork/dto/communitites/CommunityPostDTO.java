package ru.senla.socialnetwork.dto.communitites;

import java.time.ZonedDateTime;
import java.util.List;

public record CommunityPostDTO(
    Long id,
    Long authorId,
    Long communityId,
    String body,
    List<String> tags,
    ZonedDateTime createdAt,
    boolean isPinned) {
}
