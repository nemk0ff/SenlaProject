package ru.senla.socialnetwork.facades.communities;

import java.util.List;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.model.general.MemberRole;

public interface CommunityMemberFacade {
  List<CommunityMemberDTO> getAll(Long communityId);

  CommunityMemberDTO joinCommunity(Long communityId, String userEmail);

  void leaveCommunity(Long communityId, String userEmail);

  CommunityMemberDTO banMember(Long communityId, String userEmail, String reason);

  CommunityMemberDTO unbanMember(Long communityId, String userEmail);

  CommunityMemberDTO changeMemberRole(Long communityId, String userEmail, MemberRole role);

  boolean isBanned(Long communityId, String userEmail);

  boolean isAdmin(Long communityId, String userEmail);

  boolean isModerator(Long communityId, String userEmail);
}
