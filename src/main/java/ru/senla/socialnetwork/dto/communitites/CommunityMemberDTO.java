package ru.senla.socialnetwork.dto.communitites;

import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.general.MemberRole;

public record CommunityMemberDTO(
    Long id,
    String email,
    ZonedDateTime joinDate,
    MemberRole role,
    Long communityId,
    Boolean isBanned,
    String bannedReason) {
}
