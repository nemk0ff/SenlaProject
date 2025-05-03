package ru.senla.socialnetwork.services.communities;

import java.util.List;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;

public interface CommunityPostService {
  CommunityPost createPost(Long communityId, CreateCommunityPostDTO dto,
                           CommunityMember author);

  void deletePost(Long communityId, Long postId);

  CommunityPost updatePost(Long communityId, Long postId, UpdateCommunityPostDTO dto);

  List<CommunityPost> getAllPosts(Long communityId);

  CommunityPost getPost(Long communityId, Long postId);
}
