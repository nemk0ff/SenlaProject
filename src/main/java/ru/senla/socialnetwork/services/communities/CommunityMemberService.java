package ru.senla.socialnetwork.services.communities;

import java.util.List;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;

public interface CommunityMemberService {
  CommunityMember get(Long communityId, String userEmail);

  List<CommunityMember> getAll(Long communityId);

  CommunityMember joinCommunity(Community community, User user);

  void leaveCommunity(CommunityMember member);

  CommunityMember banMember(CommunityMember member, String reason);

  CommunityMember unbanMember(CommunityMember member);

  CommunityMember changeMemberRole(CommunityMember member, MemberRole role);

  boolean isMember(Long communityId, String userEmail);

  void checkIsBanned(Long communityId, String userEmail);

  void checkIsAdmin(Long communityId, String userEmail);

  void checkIsAdminOrModer(Long communityId, String userEmail);
}
