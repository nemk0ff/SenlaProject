package ru.senla.socialnetwork.dto.chats;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import java.util.List;

public record ChatDTO(
    Long id,
    String name,
    boolean isGroup,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss XXX")
    ZonedDateTime createdAt,
    List<ChatMemberDTO> members) {
}