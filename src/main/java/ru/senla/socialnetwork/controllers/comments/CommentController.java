package ru.senla.socialnetwork.controllers.comments;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;

public interface CommentController {
  ResponseEntity<?> getAll();

  ResponseEntity<?> get(Long id);

  ResponseEntity<?> getPostComments(Long postId, Authentication auth);

  ResponseEntity<?> createComment(CreateCommentDTO request);

  ResponseEntity<?> updateComment(Long id, UpdateCommentDTO request);

  ResponseEntity<?> deleteComment(Long id);
}
