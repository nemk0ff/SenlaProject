package ru.senla.socialnetwork.controllers.comments;

import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.model.comment.Reaction;

public interface CommentController {
  ResponseEntity<?> getAllComments();

  ResponseEntity<?> getComment(Long id);

  ResponseEntity<?> getAllReactions(Long postId);

  ResponseEntity<?> createComment(CommentDTO request);

  ResponseEntity<?> updateComment(UpdateCommentDTO request);

  ResponseEntity<?> deleteComment(Long id);

  ResponseEntity<?> react(Long Id, Reaction reaction);

  ResponseEntity<?> unreact(Long id);

  ResponseEntity<?> getAllPostComments(Long id);
}
