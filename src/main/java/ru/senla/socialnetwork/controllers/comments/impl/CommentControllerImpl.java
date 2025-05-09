package ru.senla.socialnetwork.controllers.comments.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import ru.senla.socialnetwork.dto.comments.CommentDTO;
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
    log.info("Администратор запросил все комментарии");
    List<CommentDTO> comments = commentFacade.getAll();
    log.info("Возвращено {} комментариев", comments.size());
    return ResponseEntity.ok(comments);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id, Authentication auth) {
    log.info("Пользователь {} запросил комментарий id={}", auth.getName(), id);
    CommentDTO comment = commentFacade.getById(id, auth.getName());
    log.info("Комментарий id={} найден. Автор: {}, пост: {}", id, comment.authorId(), comment.postId());
    return ResponseEntity.ok(comment);
  }

  @Override
  @GetMapping("/post/{id}")
  public ResponseEntity<?> getPostComments(
      @PathVariable("id") Long postId,
      Authentication auth) {
    log.info("Пользователь {} запросил комментарии к посту id={}", auth.getName(), postId);
    List<CommentDTO> comments = commentFacade.getPostComments(postId, auth.getName());
    log.info("Найдено {} комментариев к посту id={}", comments.size(), postId);
    return ResponseEntity.ok(comments);
  }

  @Override
  @PostMapping
  public ResponseEntity<?> createComment(
      CreateCommentDTO request,
      Authentication auth) {
    log.info("Пользователь {} создает комментарий к посту ID {}", auth.getName(), request.postId());
    CommentDTO comment = commentFacade.create(request, auth.getName());
    log.info("Создан комментарий id={} к посту id={}", comment.id(), request.postId());
    return ResponseEntity.status(HttpStatus.CREATED).body(comment);
  }

  @Override
  @PutMapping("/{id}")
  public ResponseEntity<?> updateComment(
      @PathVariable("id") Long id,
      UpdateCommentDTO request,
      Authentication auth) {
    log.info("Пользователь {} обновляет комментарий id={}", auth.getName(), id);
    CommentDTO updatedComment = commentFacade.update(request, auth.getName());
    log.info("Комментарий id={} успешно обновлен", id);
    return ResponseEntity.ok(updatedComment);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteComment(
      @PathVariable("id") Long id,
      Authentication auth) {
    log.info("Пользователь {} удаляет комментарий id={}", auth.getName(), id);
    commentFacade.delete(id, auth.getName());
    log.info("Комментарий id={} успешно удален", id);
    return ResponseEntity.ok("Комментарий " + id + " удален");
  }
}
