package ru.senla.socialnetwork.dto.communitites;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.MemberRole;

@Schema(description = "Информация об участнике сообщества")
public record CommunityMemberDTO(
    @Schema(description = "ID записи", example = "1")
    Long id,

    @Schema(description = "Email участника", example = "example@senla.ru")
    String email,

    @Schema(description = "Роль в сообществе", example = "MEMBER")
    MemberRole role,

    @Schema(description = "ID сообщества", example = "5")
    Long communityId,

    @Schema(description = "Дата вступления", example = "2025-01-01T12:00:00+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime joinDate,

    @Schema(description = "Дата выхода", example = "2025-02-11T23:59:59+03:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime leaveDate,

    @Schema(description = "Заблокирован ли участник", example = "false")
    Boolean isBanned,

    @Schema(description = "Причина блокировки", example = "Нарушение правил")
    String bannedReason
) {}