package ru.senla.socialnetwork.controllers.communities;

import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;

public interface CommunityController {
  ResponseEntity<CommunityDTO> create(CreateCommunityDTO dto);

  ResponseEntity<Void> delete(Long id);

  ResponseEntity<CommunityDTO> get(Long id);

  ResponseEntity<CommunityDTO> change(ChangeCommunityDTO changeCommunityDTO);
}
