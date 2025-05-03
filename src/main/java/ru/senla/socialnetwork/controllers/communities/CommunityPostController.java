package ru.senla.socialnetwork.controllers.communities;

import java.util.List;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;

public interface CommunityPostController {
  ResponseEntity<CommunityPostDTO> create(
      Long communityId, CreateCommunityPostDTO dto, String authorEmail);

  ResponseEntity<String> delete(Long communityId, Long postId, String memberEmail);

  ResponseEntity<CommunityPostDTO> update(Long communityId, Long postId,
      UpdateCommunityPostDTO dto, String memberEmail);

  ResponseEntity<List<CommunityPostDTO>> getAllPosts(Long communityId);

  ResponseEntity<CommunityPostDTO> getById(Long communityId, Long postId);
}
