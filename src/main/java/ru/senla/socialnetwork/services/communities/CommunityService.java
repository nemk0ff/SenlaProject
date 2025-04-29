package ru.senla.socialnetwork.services.communities;

import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;

public interface CommunityService {
  CommunityDTO create(CreateCommunityDTO communityDTO);

  void delete(Long communityId);

  CommunityDTO get(Long communityId);
}
