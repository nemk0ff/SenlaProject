package ru.senla.socialnetwork.controllers.comments;

import org.springframework.http.ResponseEntity;

public interface CommentController {
  ResponseEntity<?> getAll();

  ResponseEntity<?> get(Long id);

  ResponseEntity<?> getPostComments(Long postId);

  ResponseEntity<?> createComment(CommentDTO request);

  ResponseEntity<?> updateComment(Long id, UpdateCommentDTO request);

  ResponseEntity<?> deleteComment(Long id);
}
