package ru.senla.socialnetwork.controllers.comments.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.comments.CommentController;
import ru.senla.socialnetwork.facades.comments.CommentFacade;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentControllerImpl implements CommentController {
  private final CommentFacade commentFacade;

  @Override
  @GetMapping
  public ResponseEntity<?> getAll() {
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id) {
  }

  @Override
  @GetMapping("/post/{id}")
  public ResponseEntity<?> getPostComments(@PathVariable("id") Long postId) {
  }

  @Override
  @PostMapping
  public ResponseEntity<?> createComment(CommentDTO request) {
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<?> updateComment(
      @PathVariable("id") Long id,
      UpdateCommentDTO request) {
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(@PathVariable("id") Long id) {
  }
}
