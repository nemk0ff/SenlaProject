package ru.senla.socialnetwork.controllers.comments.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.comments.ReactionController;
import ru.senla.socialnetwork.dto.comments.CreateReactionDTO;
import ru.senla.socialnetwork.facades.comments.ReactionFacade;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/reactions")
public class ReactionControllerImpl implements ReactionController {
  private final ReactionFacade reactionFacade;

  @Override
  @GetMapping
  public ResponseEntity<?> getAll() {
    log.info("Получение всех реакций");
    return ResponseEntity.ok(reactionFacade.getAll());
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id) {
    log.info("Получение реакции по id {}", id);
    return ResponseEntity.ok(reactionFacade.get(id));
  }

  @Override
  @GetMapping("/{id}/reactions")
  public ResponseEntity<?> getByComment(@PathVariable("id") Long id) {
    log.info("Получение всех реакций комментария {}", id);
    return ResponseEntity.ok(reactionFacade.getByComment(id));
  }

  @Override
  @PostMapping("/{id}/reaction")
  public ResponseEntity<?> react(
      @PathVariable("id") Long id,
      CreateReactionDTO request) {
    log.info("Создание реакции на пост {}: {}", id, request);
    return ResponseEntity.ok(reactionFacade.setReaction(id, request));
  }

  @Override
  @DeleteMapping("/{id}/reaction")
  public ResponseEntity<?> react(@PathVariable("id") Long id,
                                 Authentication auth) {
    log.info("Удаление реакции по id {}", id);
    reactionFacade.removeReaction(id, auth.getName());
    return ResponseEntity.ok("Реакция на комментарий удалена");
  }
}
