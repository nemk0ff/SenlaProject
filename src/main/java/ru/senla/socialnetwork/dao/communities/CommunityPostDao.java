package ru.senla.socialnetwork.dao.communities;

import java.util.List;
import ru.senla.socialnetwork.dao.GenericDao;
import ru.senla.socialnetwork.model.communities.CommunityPost;

public interface CommunityPostDao extends GenericDao<CommunityPost> {
  List<CommunityPost> findAllByCommunity(Long communityId);

  List<CommunityPost> findPinnedByCommunity(Long communityId);
}
