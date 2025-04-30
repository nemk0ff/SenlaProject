package ru.senla.socialnetwork.dao.communities;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.communities.CommunityMember;

public interface CommunityMemberDao extends GenericDao<CommunityMember> {
  Optional<CommunityMember> findByCommunityAndUser(Long communityId, Long memberId);

  List<CommunityMember> findAllByCommunity(Long communityId);
}
