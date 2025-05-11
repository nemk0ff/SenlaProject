package ru.senla.socialnetwork.controllers.comments;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.model.comment.ReactionType;

public interface ReactionController {
  ResponseEntity<?> getAll();

  ResponseEntity<?> get(Long id, Authentication auth);

  ResponseEntity<?> getByComment(Long commentId, Authentication auth);

  ResponseEntity<?> createReaction(Long commentId, ReactionType reaction, Authentication auth);

  ResponseEntity<?> removeReaction(Long id, Authentication auth);
}
