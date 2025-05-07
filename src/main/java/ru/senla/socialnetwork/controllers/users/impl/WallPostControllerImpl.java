package ru.senla.socialnetwork.controllers.users.impl;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.dto.users.WallPostRequestDTO;
import ru.senla.socialnetwork.dto.users.WallPostResponseDTO;
import ru.senla.socialnetwork.facades.wallpost.WallPostFacade;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class WallPostControllerImpl {
  private final WallPostFacade wallPostFacade;

  @GetMapping
  public ResponseEntity<List<WallPostResponseDTO>> getAll(
      @RequestParam("email") String email,
      Authentication auth) {
    log.info("Получение всех постов пользователя {}", email);
    return ResponseEntity.ok(wallPostFacade.getByUser(email, auth.getName()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<WallPostResponseDTO> getById(@PathVariable("id") Long postId,
                                                     Authentication auth) {
    log.info("Получение поста пользователя по id поста {}", postId);
    return ResponseEntity.ok(wallPostFacade.getById(postId, auth.getName()));
  }

  @PostMapping
  public ResponseEntity<WallPostResponseDTO> create(
      @Valid @RequestBody WallPostRequestDTO dto,
      Authentication auth) {
    WallPostResponseDTO createdPost = wallPostFacade.create(dto, auth.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<String> delete(
      @PathVariable Long postId,
      Authentication auth) {
    log.info("Удаление поста {} пользователем {}", postId, auth.getName());
    wallPostFacade.delete(postId, auth.getName());
    return ResponseEntity.ok("Пост " + postId + " удалён");
  }

  @PatchMapping("/{postId}")
  public ResponseEntity<WallPostResponseDTO> update(
      @PathVariable("postId") Long postId,
      @Valid @RequestBody WallPostRequestDTO dto,
      Authentication auth) {
    WallPostResponseDTO updatedPost = wallPostFacade.update(postId, dto, auth.getName());
    return ResponseEntity.ok(updatedPost);
  }
}