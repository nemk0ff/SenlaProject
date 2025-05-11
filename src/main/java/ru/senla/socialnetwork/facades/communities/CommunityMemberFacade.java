package ru.senla.socialnetwork.facades.communities;

import java.util.List;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.model.MemberRole;

public interface CommunityMemberFacade {
  List<CommunityMemberDTO> getAll(Long communityId, String clientEmail);

  CommunityMemberDTO joinCommunity(Long communityId, String userEmail);

  CommunityMemberDTO leaveCommunity(Long communityId, String userEmail);

  CommunityMemberDTO banMember(Long communityId, String userEmail, String reason, String clientEmail);

  CommunityMemberDTO unbanMember(Long communityId, String userEmail, String clientEmail);

  CommunityMemberDTO changeMemberRole(Long communityId, String userEmail, MemberRole role,
                                      String clientEmail);
}
