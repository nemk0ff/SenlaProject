package ru.senla.socialnetwork.controllers.comments;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;

public interface CommentController {
  ResponseEntity<?> getAll();

  ResponseEntity<?> get(Long id, Authentication auth);

  ResponseEntity<?> getPostComments(Long postId, Authentication auth);

  ResponseEntity<?> createComment(CreateCommentDTO request, Authentication auth);

  ResponseEntity<?> updateComment(Long id, UpdateCommentDTO request, Authentication auth);

  ResponseEntity<?> deleteComment(Long id, Authentication auth);
}
