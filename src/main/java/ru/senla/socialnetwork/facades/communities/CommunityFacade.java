package ru.senla.socialnetwork.facades.communities;

import java.util.List;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;

public interface CommunityFacade {
  CommunityDTO create(CreateCommunityDTO communityDTO, String clientEmail);

  void delete(Long communityId, String clientEmail);

  CommunityDTO get(Long communityId, String clientEmail);

  List<CommunityDTO> getAll();

  CommunityDTO change(ChangeCommunityDTO communityDTO, String clientEmail);
}
