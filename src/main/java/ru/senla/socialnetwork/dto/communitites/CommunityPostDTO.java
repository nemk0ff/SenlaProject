package ru.senla.socialnetwork.dto.communitites;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.PostDTO;

public record CommunityPostDTO(
    Long id,
    String authorEmail,
    Long communityId,
    String body,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime createdAt,
    boolean isPinned) implements PostDTO {
}
