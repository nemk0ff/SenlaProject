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
import ru.senla.socialnetwork.facades.wallposts.WallPostFacade;

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
    log.info("Запрос всех постов пользователя {} от {}", email, auth.getName());
    List<WallPostResponseDTO> posts = wallPostFacade.getByUser(email, auth.getName());
    log.info("Найдено {} постов пользователя {}", posts.size(), email);
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/{id}")
  public ResponseEntity<WallPostResponseDTO> getById(@PathVariable("id") Long postId,
                                                     Authentication auth) {
    log.info("Запрос поста с id={} от {}", postId, auth.getName());
    WallPostResponseDTO post = wallPostFacade.getById(postId, auth.getName());
    log.info("Пост найден: id={}, автор={}", postId, post.wall_owner_id());
    return ResponseEntity.ok(post);
  }

  @PostMapping
  public ResponseEntity<WallPostResponseDTO> create(
      @Valid @RequestBody WallPostRequestDTO dto,
      Authentication auth) {
    log.info("Создание нового поста пользователем {}", auth.getName());
    WallPostResponseDTO createdPost = wallPostFacade.create(dto, auth.getName());
    log.info("Пост создан успешно: id={}", createdPost.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<String> delete(
      @PathVariable Long postId,
      Authentication auth) {
    log.info("Удаление поста id={} пользователем={}", postId, auth.getName());
    wallPostFacade.delete(postId, auth.getName());
    log.info("Пост id={} удален успешно", postId);
    return ResponseEntity.ok("Пост " + postId + " удален");
  }

  @PatchMapping("/{postId}")
  public ResponseEntity<WallPostResponseDTO> update(
      @PathVariable("postId") Long postId,
      @Valid @RequestBody WallPostRequestDTO dto,
      Authentication auth) {
    log.info("Обновление поста id={} пользователем {}", postId, auth.getName());
    WallPostResponseDTO updatedPost = wallPostFacade.update(postId, dto, auth.getName());
    log.info("Пост id={} обновлен успешно", postId);
    return ResponseEntity.ok(updatedPost);
  }
}