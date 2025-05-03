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
import ru.senla.socialnetwork.exceptions.communities.CommunityPostException;
import ru.senla.socialnetwork.facades.communities.CommunityPostFacade;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.communities.CommunityPost;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.common.CommonService;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.communities.CommunityPostService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommunityPostFacadeImpl implements CommunityPostFacade {
  private final CommunityPostService communityPostService;
  private final CommunityMemberService communityMemberService;
  private final CommonService commonService;

  @Override
  @Transactional
  public CommunityPostDTO createPost(Long communityId, CreateCommunityPostDTO dto, String authorEmail) {
    User author = commonService.getUserByEmail(authorEmail);
    CommunityMember member = communityMemberService.get(communityId, author.getId());

    return CommunityPostMapper.INSTANCE
        .toDTO(communityPostService.createPost(communityId, dto, member));
  }

  @Override
  @Transactional
  public void deletePost(Long communityId, Long postId, String requesterEmail) {
    checkAccess(communityId, postId, requesterEmail);

    communityPostService.deletePost(communityId, postId);
  }

  @Override
  @Transactional
  public CommunityPostDTO updatePost(Long communityId, Long postId, UpdateCommunityPostDTO dto, String requesterEmail) {
    checkAccess(communityId, postId, requesterEmail);

    return CommunityPostMapper.INSTANCE
        .toDTO(communityPostService.updatePost(communityId, postId, dto));
  }

  private void checkAccess(Long communityId, Long postId, String requesterEmail) {
    User user = commonService.getUserByEmail(requesterEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    CommunityPost post = communityPostService.getPost(communityId, postId);

    if(member.getRole().equals(MemberRole.MEMBER) && post.getAuthor().equals(member)) {
      throw new CommunityPostException("Недостаточно прав для управления этим постом");
    }
  }

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
}
