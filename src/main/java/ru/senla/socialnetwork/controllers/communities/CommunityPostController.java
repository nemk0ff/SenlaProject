package ru.senla.socialnetwork.controllers.communities;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;

public interface CommunityPostController {
  ResponseEntity<?> getAllPosts(Long communityId);

  ResponseEntity<?> getPinnedPosts(Long communityId);

  ResponseEntity<?> getById(Long communityId, Long postId);

  ResponseEntity<?> create(Long communityId, CreateCommunityPostDTO dto, Authentication auth);

  ResponseEntity<?> delete(Long communityId, Long postId, Authentication auth);

  ResponseEntity<?> update(Long communityId, Long postId, UpdateCommunityPostDTO dto,
                           Authentication auth);
}
