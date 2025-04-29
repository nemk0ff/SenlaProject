package ru.senla.socialnetwork.services.communities;

import ru.senla.socialnetwork.model.communities.Community;

public interface CommunityService {
  Community get(Long communityId);

  Community save(Community communityToSave);

  void delete(Community communityToDelete);
}
