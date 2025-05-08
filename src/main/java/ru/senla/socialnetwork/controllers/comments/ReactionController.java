package ru.senla.socialnetwork.controllers.comments;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.comments.CreateReactionDTO;

public interface ReactionController {
  ResponseEntity<?> getAll();

  ResponseEntity<?> get(Long id, Authentication auth);

  ResponseEntity<?> react(Long commentId, CreateReactionDTO request);

  ResponseEntity<?> react(Long id, Authentication auth);

  ResponseEntity<?> getByComment(Long id);
}
