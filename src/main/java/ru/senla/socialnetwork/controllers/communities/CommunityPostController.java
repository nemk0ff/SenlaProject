package ru.senla.socialnetwork.controllers.communities;

import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;

public interface CommunityPostController {
  ResponseEntity<?> create(Long communityId, CreateCommunityPostDTO dto, String authorEmail);

  ResponseEntity<?> delete(Long communityId, Long postId, String memberEmail);

  ResponseEntity<?> update(Long communityId, Long postId, UpdateCommunityPostDTO dto, String memberEmail);

  ResponseEntity<?> getAllPosts(Long communityId);

  ResponseEntity<?> getById(Long communityId, Long postId);
}
