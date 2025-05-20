package ru.senla.socialnetwork.dto.chats;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;

@Schema(description = "Информация о чате")
public record ChatDTO(
    @Schema(description = "ID чата", example = "1")
    Long id,

    @Schema(description = "Название чата", example = "День рождения")
    String name,

    @Schema(description = "Флаг группового чата", example = "true")
    boolean isGroup,

    @Schema(description = "Дата создания чата", example = "2025-01-31T12:00:00+03:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime createdAt,

    @Schema(description = "Участники чата")
    List<ChatMemberDTO> members
) {
}