package ru.senla.socialnetwork.dto.communitites;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.general.MemberRole;

public record CommunityMemberDTO(
    Long id,
    String email,
    MemberRole role,
    Long communityId,

    @JsonFormat(shape = JsonFormat.Shape.STRING,
        pattern = "dd-MM-yyyy HH:mm:ss [ZZZ]",
        timezone = "UTC")
    ZonedDateTime joinDate,

    @JsonFormat(shape = JsonFormat.Shape.STRING,
        pattern = "dd-MM-yyyy HH:mm:ss [ZZZ]",
        timezone = "UTC")
    ZonedDateTime leaveDate,

    Boolean isBanned,
    String bannedReason) {
}
