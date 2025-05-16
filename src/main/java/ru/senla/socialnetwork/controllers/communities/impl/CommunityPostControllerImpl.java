package ru.senla.socialnetwork.controllers.communities.impl;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityPostController;
import ru.senla.socialnetwork.dto.DeleteResponseDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.facades.communities.CommunityPostFacade;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities/{communityId}/posts")
public class CommunityPostControllerImpl implements CommunityPostController {
  private final CommunityPostFacade communityPostFacade;

  @Override
  @GetMapping
  public ResponseEntity<?> getAllPosts(
      @PathVariable Long communityId) {
    log.info("Запрос всех постов сообщества id={}", communityId);
    List<CommunityPostDTO> posts = communityPostFacade.getAllPosts(communityId);
    log.info("Возвращено {} постов сообщества id={}", posts.size(), communityId);
    return ResponseEntity.ok(posts);
  }

  @Override
  @GetMapping("/pinned")
  public ResponseEntity<?> getPinnedPosts(
      @PathVariable Long communityId) {
    log.info("Запрос закреплённых постов сообщества id={}", communityId);
    List<CommunityPostDTO> posts = communityPostFacade.getPinnedPosts(communityId);
    log.info("Возвращено {} постов, закреплённых в сообществе id={}", posts.size(), communityId);
    return ResponseEntity.ok(posts);
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> getById(
      @PathVariable Long communityId,
      @PathVariable("id") Long postId) {
    log.info("Запрос поста id={} в сообществе id={}", postId, communityId);
    CommunityPostDTO post = communityPostFacade.getPost(communityId, postId);
    log.info("Пост id={} найден. Автор: {}", postId, post.authorEmail());
    return ResponseEntity.ok(post);
  }

  @Override
  @PostMapping
  public ResponseEntity<?> create(
      @PathVariable Long communityId,
      @Valid @RequestBody CreateCommunityPostDTO dto,
      Authentication auth) {
    log.info("Создание поста в сообществе id={} пользователем {}", communityId, auth.getName());
    CommunityPostDTO createdPost = communityPostFacade.createPost(communityId, dto, auth.getName());
    log.info("Создан пост id={} в сообществе id={}. Автор: {}",
        createdPost.id(), communityId, auth.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
  }

  @Override
  @DeleteMapping("/{postId}")
  public ResponseEntity<?> delete(
      @PathVariable Long communityId,
      @PathVariable Long postId,
      Authentication auth) {
    log.warn("Удаление поста id={} из сообщества id={}. Инициатор: {}",
        postId, communityId, auth.getName());
    communityPostFacade.deletePost(communityId, postId, auth.getName());
    log.warn("Пост id={} удален из сообщества id={}", postId, communityId);
    return ResponseEntity.ok(new DeleteResponseDTO(
        "Пост сообщества успешно удалён",
        Map.of(
            "communityId", communityId,
            "postId", postId)));
  }

  @Override
  @PatchMapping("/{postId}")
  public ResponseEntity<?> update(
      @PathVariable Long communityId,
      @PathVariable Long postId,
      @Valid @RequestBody UpdateCommunityPostDTO dto,
      Authentication auth) {
    log.info("Обновление поста id={} в сообществе id={}. Инициатор: {}",
        postId, communityId, auth.getName());
    CommunityPostDTO updatedPost = communityPostFacade.updatePost(communityId, postId, dto, auth.getName());
    log.info("Пост id={} успешно обновлен", postId);
    return ResponseEntity.ok(updatedPost);
  }
}
