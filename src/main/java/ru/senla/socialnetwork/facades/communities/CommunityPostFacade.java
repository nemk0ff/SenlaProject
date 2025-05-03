package ru.senla.socialnetwork.facades.communities;

import java.util.List;
import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;

public interface CommunityPostFacade {
  CommunityPostDTO createPost(Long communityId, CreateCommunityPostDTO dto, String authorEmail);

  void deletePost(Long communityId, Long postId, String requesterEmail);

  CommunityPostDTO updatePost(Long communityId, Long postId, UpdateCommunityPostDTO dto,
                              String requesterEmail);

  List<CommunityPostDTO> getAllPosts(Long communityId);

  CommunityPostDTO getPost(Long communityId, Long postId);
}
