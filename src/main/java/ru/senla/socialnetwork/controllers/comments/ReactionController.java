package ru.senla.socialnetwork.controllers.comments;

import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.model.comment.ReactionType;

public interface ReactionController {
  ResponseEntity<?> getAll();

  ResponseEntity<?> get(Long id);

  ResponseEntity<?> react(Long commentId, ReactionType reaction);

  ResponseEntity<?> react(Long id);

  ResponseEntity<?> getReactions(Long id);
}
