package ru.senla.socialnetwork.facade.communities.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.dto.mappers.CommunityMemberMapper;
import ru.senla.socialnetwork.exceptions.communities.CommunityException;
import ru.senla.socialnetwork.facade.communities.CommunityMemberFacade;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.common.CommonService;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.communities.CommunityService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityMemberFacadeImpl implements CommunityMemberFacade {
  private final CommunityService communityService;
  private final CommunityMemberService communityMemberService;
  private final CommonService commonService;

  @Override
  public List<CommunityMemberDTO> getAll(Long communityId) {
    return CommunityMemberMapper.INSTANCE.toListDTO(communityMemberService.getAll(communityId));
  }

  @Override
  public CommunityMemberDTO joinCommunity(Long communityId, String userEmail) {
    User user = commonService.getUserByEmail(userEmail);
    Community community = communityService.get(communityId);

    if(communityMemberService.isMember(communityId, user.getId())) {
      throw new CommunityException(userEmail + " уже является участником сообщества");
    }
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.joinCommunity(community, user));
  }

  @Override
  public void leaveCommunity(Long communityId, String userEmail) {
    User user = commonService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    communityMemberService.leaveCommunity(member);
  }

  @Override
  public CommunityMemberDTO banMember(Long communityId, String userEmail, String reason) {
    User user = commonService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.banMember(member, reason));
  }

  @Override
  public CommunityMemberDTO changeMemberRole(Long communityId, String userEmail, MemberRole role) {
    User user = commonService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.changeMemberRole(member, role));
  }

  @Override
  public boolean isBanned(Long communityId, String userEmail) {
    User user = commonService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    return member.getIsBanned();
  }
}
