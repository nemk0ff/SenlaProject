package ru.senla.socialnetwork.controllers.communities.impl;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityPostController;
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
    return ResponseEntity.ok(communityPostFacade.getAllPosts(communityId));
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<?> getById(
      @PathVariable Long communityId,
      @PathVariable("id") Long postId) {
    return ResponseEntity.ok(communityPostFacade.getPost(communityId, postId));
  }

  @PostMapping
  @PreAuthorize("#authorEmail=authentication.name")
  public ResponseEntity<?> create(
      @PathVariable Long communityId,
      @Valid @RequestBody CreateCommunityPostDTO dto,
      @RequestParam String authorEmail) {
    CommunityPostDTO createdPost = communityPostFacade.createPost(communityId, dto, authorEmail);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<?> delete(
      @PathVariable Long communityId,
      @PathVariable Long postId,
      @RequestParam String email) {
    communityPostFacade.deletePost(communityId, postId, email);
    return ResponseEntity.ok("Пост " + postId + " удалён");
  }

  @PatchMapping("/{postId}")
  public ResponseEntity<?> update(
      @PathVariable Long communityId,
      @PathVariable Long postId,
      @Valid @RequestBody UpdateCommunityPostDTO dto,
      @RequestParam String email) {
    CommunityPostDTO updatedPost = communityPostFacade.updatePost(communityId, postId, dto, email);
    return ResponseEntity.ok(updatedPost);
  }
}
