package ru.senla.socialnetwork.dto.communitites;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.MemberRole;

public record CommunityMemberDTO(
    Long id,
    String email,
    MemberRole role,
    Long communityId,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime joinDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    ZonedDateTime leaveDate,
    Boolean isBanned,
    String bannedReason) {
}
