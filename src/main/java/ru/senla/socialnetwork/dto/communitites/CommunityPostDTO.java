package ru.senla.socialnetwork.dto.communitites;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;

public record CommunityPostDTO(
    Long id,
    Long authorId,
    Long communityId,
    String body,
    @JsonFormat(shape = JsonFormat.Shape.STRING,
        pattern = "dd-MM-yyyy HH:mm:ss [ZZZ]",
        timezone = "UTC")
    ZonedDateTime createdAt,
    boolean isPinned) {
}
