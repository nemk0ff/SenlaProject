package ru.senla.socialnetwork.controllers.comments;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.model.comment.Reaction;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentControllerImpl implements CommentController {

  @Override
  @GetMapping
  public ResponseEntity<?> getAllComments() {
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> getComment(Long id) {
  }

  @Override
  @GetMapping("/post/{id}")
  public ResponseEntity<?> getAllPostComments(Long postId) {
  }

  @Override
  @PostMapping
  public ResponseEntity<?> createComment(CommentDTO request) {
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<?> updateComment(UpdateCommentDTO request) {
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(Long id) {
  }

  @Override
  @PostMapping("/{id}/reaction")
  public ResponseEntity<?> react(Long Id,
      @RequestParam Reaction reaction) {
  }

  @Override
  @DeleteMapping("/{id}/reaction")
  public ResponseEntity<?> unreact(Long id) {
  }

  @Override
  @GetMapping("/{id}/reactions")
  public ResponseEntity<?> getAllReactions(Long id) {
  }
}
