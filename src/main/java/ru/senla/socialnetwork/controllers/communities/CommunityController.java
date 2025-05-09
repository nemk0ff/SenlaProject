package ru.senla.socialnetwork.controllers.communities;

import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;

public interface CommunityController {
  ResponseEntity<?> create(CreateCommunityDTO dto);

  ResponseEntity<?> delete(Long id);

  ResponseEntity<?> get(Long id);

  ResponseEntity<?> change(ChangeCommunityDTO changeCommunityDTO);
}
