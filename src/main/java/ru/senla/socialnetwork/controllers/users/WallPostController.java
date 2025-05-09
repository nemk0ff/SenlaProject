package ru.senla.socialnetwork.controllers.users;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;

public interface WallPostController {
  ResponseEntity<List<?>> getAll(String email, Authentication auth);

  ResponseEntity<?> getById(Long postId, Authentication auth);

  ResponseEntity<?> create(WallPostRequestDTO dto, Authentication auth);

  ResponseEntity<?> delete(Long postId, Authentication auth);

  ResponseEntity<?> update(Long postId, WallPostRequestDTO dto, Authentication auth);
}
