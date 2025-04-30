package ru.senla.socialnetwork.facade.communities.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dto.communitites.CommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityPostDTO;
import ru.senla.socialnetwork.dto.communitites.UpdateCommunityPostDTO;
import ru.senla.socialnetwork.dto.mappers.CommunityPostMapper;
import ru.senla.socialnetwork.facade.communities.CommunityPostFacade;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.common.CommonService;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.communities.CommunityPostService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityPostFacadeImpl implements CommunityPostFacade {
  private final CommunityPostService communityPostService;
  private final CommunityMemberService communityMemberService;
  private final CommonService commonService;

  @Override
  public CommunityPostDTO createPost(Long communityId, CreateCommunityPostDTO dto, String authorEmail) {
    User author = commonService.getUserByEmail(authorEmail);
    CommunityMember member = communityMemberService.get(communityId, author.getId());

    return CommunityPostMapper.INSTANCE
        .toDTO(communityPostService.createPost(communityId, dto, member));
  }

  @Override
  public void deletePost(Long communityId, Long postId, String requesterEmail) {
    User requester = commonService.getUserByEmail(requesterEmail);
    communityPostService.deletePost(communityId, postId, requester.getId());
  }

  @Override
  public CommunityPostDTO updatePost(Long communityId, Long postId, UpdateCommunityPostDTO dto, String requesterEmail) {
    User requester = commonService.getUserByEmail(requesterEmail);

    return CommunityPostMapper.INSTANCE
        .toDTO(communityPostService.updatePost(communityId, postId, dto, requester.getId()));
  }

  @Override
  public List<CommunityPostDTO> getAllPosts(Long communityId, Boolean pinnedOnly) {
    return CommunityPostMapper.INSTANCE
        .toListDTO(communityPostService.getAllPosts(communityId, pinnedOnly));
  }
}
