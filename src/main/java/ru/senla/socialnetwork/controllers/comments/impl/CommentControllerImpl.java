package ru.senla.socialnetwork.controllers.comments.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.comments.CommentController;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;
import ru.senla.socialnetwork.facades.comments.CommentFacade;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentControllerImpl implements CommentController {
  private final CommentFacade commentFacade;

  @Override
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAll() {
    log.info("Получение всех комментариев");
    return ResponseEntity.ok(commentFacade.getAll());
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id, Authentication auth) {
    log.info("Получение комментария по id {} пользователем {}", id, auth.getName());
    return ResponseEntity.ok(commentFacade.getById(id, auth.getName()));
  }

  @Override
  @GetMapping("/post/{id}")
  public ResponseEntity<?> getPostComments(
      @PathVariable("id") Long postId,
      Authentication auth) {
    log.info("Получение всех комментариев по id поста {}", postId);
    return ResponseEntity.ok(commentFacade.getPostComments(postId, auth.getName()));
  }

  @Override
  @PostMapping
  public ResponseEntity<?> createComment(CreateCommentDTO request) {
    log.info("Создание нового комментария {}", request);
    return ResponseEntity.ok(commentFacade.create(request));
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<?> updateComment(
      @PathVariable("id") Long id,
      UpdateCommentDTO request) {
    log.info("Редактирование комментария {}", id);
    return ResponseEntity.ok(commentFacade.update(request));
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(@PathVariable("id") Long id) {
    return ResponseEntity.ok("Удалено");
  }
}
