package ru.senla.socialnetwork.facades.communities;

import java.util.List;
import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;

public interface CommunityPostFacade {
  List<CommunityPostDTO> getAllPosts(Long communityId);

  List<CommunityPostDTO> getPinnedPosts(Long communityId);

  CommunityPostDTO getPost(Long communityId, Long postId);

  CommunityPostDTO createPost(Long communityId, CreateCommunityPostDTO dto, String clientEmail);

  void deletePost(Long communityId, Long postId, String clientEmail);

  CommunityPostDTO updatePost(Long communityId, Long postId, UpdateCommunityPostDTO dto,
                              String requesterEmail);
}
