package ru.senla.socialnetwork.dto.chats;

import java.time.ZonedDateTime;
import java.util.List;

public record ChatDTO(
    Long id,
    String name,
    boolean isGroup,
    ZonedDateTime createdAt,
    List<ChatMemberDTO> members) {
}