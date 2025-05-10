package ru.senla.socialnetwork.controllers.communities;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;

public interface CommunityController {
  ResponseEntity<?> create(CreateCommunityDTO dto, Authentication auth);

  ResponseEntity<?> delete(Long id, Authentication auth);

  ResponseEntity<?> get(Long id);

  ResponseEntity<?> change(ChangeCommunityDTO changeCommunityDTO, Authentication auth);
}
