package ru.senla.socialnetwork.dto.communitites;

import java.time.ZonedDateTime;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;

public record CommunityMemberDTO(
    Long id,
    User user,
    ZonedDateTime joinDate,
    MemberRole role,
    Community community,
    Boolean isBanned,
    String bannedReason) {
}
