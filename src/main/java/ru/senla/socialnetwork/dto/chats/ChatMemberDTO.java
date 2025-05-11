package ru.senla.socialnetwork.dto.chats;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.MemberRole;

public record ChatMemberDTO(
    String email,
    Long chatId,
    String chatName,
    MemberRole role,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime joinDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime leaveDate) {
}
