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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.comments.ReactionController;
import ru.senla.socialnetwork.dto.comments.CreateReactionDTO;
import ru.senla.socialnetwork.dto.comments.ReactionDTO;
import ru.senla.socialnetwork.facades.comments.ReactionFacade;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/reactions")
public class ReactionControllerImpl implements ReactionController {
  private final ReactionFacade reactionFacade;

  @Override
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAll() {
    log.info("Администратор запросил список всех реакций");
    List<ReactionDTO> reactions = reactionFacade.getAll();
    log.info("Возвращено {} реакций", reactions.size());
    return ResponseEntity.ok(reactions);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> get(
      @PathVariable("id") Long id,
      Authentication auth) {
    log.info("Пользователь {} запросил реакцию id{}", auth.getName(), id);
    ReactionDTO reaction = reactionFacade.getById(id, auth.getName());
    log.info("Реакция id={} найдена. Тип={}, комментарий={}, пользователь={}",
        id, reaction.type(), reaction.commentId(), reaction.email());
    return ResponseEntity.ok(reaction);
  }

  @Override
  @GetMapping("/{id}/reactions")
  public ResponseEntity<?> getByComment(
      @PathVariable("id") Long commentId,
      Authentication auth) {
    log.info("Пользователь {} запросил реакции для комментария id={}", auth.getName(), commentId);
    List<ReactionDTO> reactions = reactionFacade.getByComment(commentId, auth.getName());
    log.info("Найдено {} реакций для комментария id={}", reactions.size(), commentId);
    return ResponseEntity.ok(reactions);
  }

  @Override
  @PostMapping("/react")
  public ResponseEntity<?> createReaction(
      CreateReactionDTO request,
      Authentication auth) {
    log.info("Пользователь {} добавляет реакцию {} к комментарию id={}",
        auth.getName(), request.type(), request.commentId());
    ReactionDTO reaction = reactionFacade.setReaction(request, auth.getName());
    log.info("Создана реакция id={} типа {} к комментарию {}",
        reaction.id(), reaction.type(), reaction.commentId());
    return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
  }

  @Override
  @DeleteMapping("/react/{id}")
  public ResponseEntity<?> removeReaction(
      @PathVariable("id") Long reactionId,
      Authentication auth) {
    log.info("Пользователь {} удаляет реакцию ID {}", auth.getName(), reactionId);
    reactionFacade.removeReaction(reactionId, auth.getName());
    log.info("Реакция ID {} успешно удалена", reactionId);
    return ResponseEntity.ok("Реакция " + reactionId + " удалена");
  }
}
