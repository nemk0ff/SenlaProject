package ru.senla.socialnetwork.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.dto.PostDTO;

public record WallPostResponseDTO(
    Long id,
    String wallOwnerEmail,
    String mood,
    String body,
    String location,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime createdAt) implements PostDTO {
}
