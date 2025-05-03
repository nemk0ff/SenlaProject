package ru.senla.socialnetwork.services.communities;

import java.util.List;
import ru.senla.socialnetwork.model.communities.Community;

public interface CommunityService {
  Community get(Long communityId);

  List<Community> getAll();

  Community save(Community communityToSave);

  void delete(Community communityToDelete);
}
