package ru.senla.socialnetwork.dto.chats;

import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.general.MemberRole;

public record ChatMemberDTO(
    String email,
    MemberRole role,
    ZonedDateTime joinDate) {
}
