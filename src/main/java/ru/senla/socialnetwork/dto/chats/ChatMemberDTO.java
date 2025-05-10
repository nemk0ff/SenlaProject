package ru.senla.socialnetwork.dto.chats;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.general.MemberRole;

public record ChatMemberDTO(
    String email,
    Long chatId,
    String chatName,
    MemberRole role,
    @JsonFormat(shape = JsonFormat.Shape.STRING,
        pattern = "dd-MM-yyyy HH:mm:ss [ZZZ]",
        timezone = "UTC")
    ZonedDateTime joinDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING,
        pattern = "dd-MM-yyyy HH:mm:ss [ZZZ]",
        timezone = "UTC")
    ZonedDateTime leaveDate) {
}
