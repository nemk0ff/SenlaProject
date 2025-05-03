package ru.senla.socialnetwork.facade.communities;

import java.util.List;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;

public interface CommunityFacade {
  CommunityDTO create(CreateCommunityDTO communityDTO);

  void delete(Long communityId);

  CommunityDTO get(Long communityId);

  List<CommunityDTO> getAll();

  CommunityDTO change(ChangeCommunityDTO communityDTO);
}
