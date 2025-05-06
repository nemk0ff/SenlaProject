package ru.senla.socialnetwork.controllers.comments.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.comments.ReactionController;
import ru.senla.socialnetwork.facades.comments.ReactionFacade;
import ru.senla.socialnetwork.model.comment.ReactionType;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/reactions")
public class ReactionControllerImpl implements ReactionController {
  private final ReactionFacade reactionFacade;

  @Override
  @GetMapping
  public ResponseEntity<?> getAll() {
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable("id") Long id) {
  }

  @Override
  @PostMapping("/{id}/reaction")
  public ResponseEntity<?> react(
      @PathVariable("id") Long Id,
      @RequestParam ReactionType reaction) {
  }

  @Override
  @DeleteMapping("/{id}/reaction")
  public ResponseEntity<?> react(@PathVariable("id") Long id) {
  }

  @Override
  @GetMapping("/{id}/reactions")
  public ResponseEntity<?> getReactions(@PathVariable("id") Long id) {
  }
}
