package ru.senla.socialnetwork.controllers.communities.impl;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ru.senla.socialnetwork.facade.communities.CommunityPostFacade;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities/{communityId}/posts")
public class CommunityPostControllerImpl implements CommunityPostController {
  private final CommunityPostFacade communityPostFacade;

  @PostMapping
  public ResponseEntity<CommunityPostDTO> create(
      @PathVariable Long communityId,
      @Valid @RequestBody CreateCommunityPostDTO dto,
      @RequestParam String authorEmail) {
    CommunityPostDTO createdPost = communityPostFacade.createPost(communityId, dto, authorEmail);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> delete(
      @PathVariable Long communityId,
      @PathVariable Long postId,
      @RequestParam String memberEmail) {
    communityPostFacade.deletePost(communityId, postId, memberEmail);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{postId}")
  public ResponseEntity<CommunityPostDTO> update(
      @PathVariable Long communityId,
      @PathVariable Long postId,
      @Valid @RequestBody UpdateCommunityPostDTO dto,
      @RequestParam String memberEmail) {
    CommunityPostDTO updatedPost = communityPostFacade.updatePost(communityId, postId, dto, memberEmail);
    return ResponseEntity.ok(updatedPost);
  }

  @Override
  @GetMapping
  public ResponseEntity<List<CommunityPostDTO>> getAllPosts(
      @PathVariable Long communityId) {
    return ResponseEntity.ok(communityPostFacade.getAllPosts(communityId));
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<CommunityPostDTO> getById(@PathVariable Long communityId,
                                                  @PathVariable("id") Long postId) {
    return ResponseEntity.ok(communityPostFacade.getById(communityId, postId));
  }
}
