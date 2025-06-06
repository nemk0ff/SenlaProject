package ru.senla.socialnetwork.services.posts;

import java.util.List;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;

public interface CommunityPostService {
  CommunityPost createPost(Community community, CreateCommunityPostDTO dto,
                           CommunityMember author);

  void deletePost(CommunityPost post);

  CommunityPost updatePost(CommunityPost post, UpdateCommunityPostDTO dto);

  List<CommunityPost> getAllPosts(Long communityId);

  List<CommunityPost> getPinnedPosts(Long communityId);

  CommunityPost getPost(Long communityId, Long postId);
}
