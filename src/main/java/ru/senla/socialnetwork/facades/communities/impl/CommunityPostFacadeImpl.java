package ru.senla.socialnetwork.facades.communities.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.dto.mappers.CommunityPostMapper;
import ru.senla.socialnetwork.facades.communities.CommunityPostFacade;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.posts.CommunityPostService;
import ru.senla.socialnetwork.services.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommunityPostFacadeImpl implements CommunityPostFacade {
  private final CommunityPostService communityPostService;
  private final CommunityMemberService communityMemberService;
  private final UserService userService;

  @Override
  @Transactional(readOnly = true)
  public List<CommunityPostDTO> getAllPosts(Long communityId) {
    return CommunityPostMapper.INSTANCE
        .toListDTO(communityPostService.getAllPosts(communityId));
  }

  @Override
  @Transactional(readOnly = true)
  public CommunityPostDTO getPost(Long communityId, Long postId) {
    return CommunityPostMapper.INSTANCE.toDTO(communityPostService.getPost(communityId, postId));
  }

  @Override
  @Transactional
  public CommunityPostDTO createPost(Long communityId, CreateCommunityPostDTO dto, String clientEmail) {
    communityMemberService.checkIsBanned(communityId, clientEmail);
    CommunityMember member = communityMemberService.get(communityId, clientEmail);
    return CommunityPostMapper.INSTANCE
        .toDTO(communityPostService.createPost(communityId, dto, member));
  }

  @Override
  @Transactional
  public void deletePost(Long communityId, Long postId, String clientEmail) {
    if(!userService.isAdmin(clientEmail)) {
      checkAccess(communityId, postId, clientEmail);
    }
    CommunityPost post = communityPostService.getPost(communityId, postId);
    communityPostService.deletePost(post);
  }

  @Override
  @Transactional
  public CommunityPostDTO updatePost(Long communityId, Long postId, UpdateCommunityPostDTO dto, String requesterEmail) {
    checkAccess(communityId, postId, requesterEmail);
    CommunityPost post = communityPostService.getPost(communityId, postId);
    return CommunityPostMapper.INSTANCE
        .toDTO(communityPostService.updatePost(post, dto));
  }

  private void checkAccess(Long communityId, Long postId, String clientEmail) {
    CommunityMember member = communityMemberService.get(communityId, clientEmail);
    CommunityPost post = communityPostService.getPost(communityId, postId);
    if(!post.getAuthor().equals(member)) {
      communityMemberService.checkIsAdminOrModer(communityId, clientEmail);
    }
  }
}
