package ru.senla.socialnetwork.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;

public record WallPostResponseDTO(
    Long id,
    String wallOwnerEmail,
    String mood,
    String body,
    String location,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss XXX")
    ZonedDateTime createdAt) {
}
