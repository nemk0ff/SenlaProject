package ru.senla.socialnetwork.controllers.comments.impl;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.comments.CommentController;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.comments.CommentDTO;
import ru.senla.socialnetwork.dto.comments.CreateCommentDTO;
import ru.senla.socialnetwork.dto.comments.UpdateCommentDTO;
import ru.senla.socialnetwork.facades.comments.CommentFacade;

@Slf4j
@RestController
@AllArgsConstructor
public class CommentControllerImpl implements CommentController {
  private final CommentFacade commentFacade;

  @Override
  @GetMapping("/comments")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAll() {
    log.info("Администратор запросил все комментарии");
    List<CommentDTO> comments = commentFacade.getAll();
    log.info("Возвращено {} комментариев", comments.size());
    return ResponseEntity.ok(comments);
  }

  @Override
  @GetMapping("/comments/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id, Authentication auth) {
    log.info("Пользователь {} запросил комментарий id={}", auth.getName(), id);
    CommentDTO comment = commentFacade.getById(id, auth.getName());
    log.info("Комментарий id={} найден. Автор: {}, пост: {}", id, comment.authorId(), comment.postId());
    return ResponseEntity.ok(comment);
  }

  @Override
  @GetMapping("/post/{id}/comments")
  public ResponseEntity<?> getPostComments(
      @PathVariable("id") Long postId,
      Authentication auth) {
    log.info("Пользователь {} запросил комментарии к посту id={}", auth.getName(), postId);
    List<CommentDTO> comments = commentFacade.getPostComments(postId, auth.getName());
    log.info("Найдено {} комментариев к посту id={}", comments.size(), postId);
    return ResponseEntity.ok(comments);
  }

  @Override
  @PostMapping(path = "/post/{id}/comments")
  public ResponseEntity<?> createComment(
      @PathVariable("id") Long postId,
      @RequestBody @Valid CreateCommentDTO request,
      Authentication auth) {
    log.info("Пользователь {} создает комментарий к посту ID {}", auth.getName(), postId);
    CommentDTO comment = commentFacade.create(postId, request, auth.getName());
    log.info("Создан комментарий id={} к посту id={}", comment.id(), postId);
    return ResponseEntity.status(HttpStatus.CREATED).body(comment);
  }

  @Override
  @PutMapping("/comments/{id}")
  public ResponseEntity<?> updateComment(
      @PathVariable("id") Long id,
      @RequestBody @Valid UpdateCommentDTO request,
      Authentication auth) {
    log.info("Пользователь {} обновляет комментарий id={}", auth.getName(), id);
    CommentDTO updatedComment = commentFacade.update(id, request, auth.getName());
    log.info("Комментарий id={} успешно обновлен", id);
    return ResponseEntity.ok(updatedComment);
  }

  @Override
  @DeleteMapping("/comments/{id}")
  public ResponseEntity<?> deleteComment(
      @PathVariable("id") Long id,
      Authentication auth) {
    log.info("Пользователь {} удаляет комментарий id={}", auth.getName(), id);
    commentFacade.delete(id, auth.getName());
    log.info("Комментарий id={} успешно удален", id);
    return ResponseEntity.ok(new DeleteResponseDTO(
        "Комментарий успешно удален",
        Map.of("commentId", id)));
  }
}