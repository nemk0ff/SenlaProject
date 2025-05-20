package ru.senla.socialnetwork.dto.chats;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.MemberRole;

@Schema(description = "Информация об участнике чата")
public record ChatMemberDTO(
    @Schema(description = "Email участника", example = "example@senla.ru")
    String email,

    @Schema(description = "ID чата", example = "1")
    Long chatId,

    @Schema(description = "Название чата", example = "День рождения")
    String chatName,

    @Schema(description = "Роль участника", example = "MEMBER")
    MemberRole role,

    @Schema(description = "Дата вступления в чат", example = "2025-01-031T12:00:00+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime joinDate,

    @Schema(description = "Дата выхода из чата", example = "2025-02-15T23:59:59+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime leaveDate
) {
}