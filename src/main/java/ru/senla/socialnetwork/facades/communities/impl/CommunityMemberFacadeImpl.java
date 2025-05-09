package ru.senla.socialnetwork.facades.communities.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.communitites.CommunityMemberDTO;
import ru.senla.socialnetwork.dto.mappers.CommunityMemberMapper;
import ru.senla.socialnetwork.exceptions.communities.CommunityMemberException;
import ru.senla.socialnetwork.facades.communities.CommunityMemberFacade;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.communities.CommunityService;
import ru.senla.socialnetwork.services.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommunityMemberFacadeImpl implements CommunityMemberFacade {
  private final CommunityService communityService;
  private final CommunityMemberService communityMemberService;
  private final UserService userService;

  @Override
  @Transactional(readOnly = true)
  public List<CommunityMemberDTO> getAll(Long communityId, String clientEmail) {
    return CommunityMemberMapper.INSTANCE.toListDTO(communityMemberService.getAll(communityId));
  }

  @Override
  public CommunityMemberDTO joinCommunity(Long communityId, String clientEmail) {
    log.info("создание участника сообщества: id={}, email={}", communityId, clientEmail);
    User user = userService.getUserByEmail(clientEmail);
    Community community = communityService.get(communityId);
    log.info("Найдены user={} и community={}", user.getId(), community.getId());

    if (communityMemberService.isMember(communityId, clientEmail)) {
      throw new CommunityMemberException(clientEmail + " уже является участником сообщества");
    }
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.joinCommunity(community, user));
  }

  @Override
  public void leaveCommunity(Long communityId, String userEmail) {
    CommunityMember member = communityMemberService.get(communityId, userEmail);
    communityMemberService.leaveCommunity(member);
  }

  @Override
  public CommunityMemberDTO banMember(Long communityId, String userEmail, String reason, String clientEmail) {
    communityMemberService.checkIsAdminOrModer(communityId, clientEmail);
    CommunityMember member = communityMemberService.get(communityId, userEmail);
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.banMember(member, reason));
  }

  @Override
  public CommunityMemberDTO unbanMember(Long communityId, String userEmail, String clientEmail) {
    communityMemberService.checkIsAdminOrModer(communityId, clientEmail);
    CommunityMember member = communityMemberService.get(communityId, userEmail);
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.unbanMember(member));
  }

  @Override
  public CommunityMemberDTO changeMemberRole(Long communityId, String userEmail, MemberRole role,
                                             String clientEmail) {
    communityMemberService.checkIsAdmin(communityId, clientEmail);
    if (clientEmail.equals(userEmail)) {
      throw new CommunityMemberException("Вы не можете изменить свою роль в сообществе");
    }
    CommunityMember member = communityMemberService.get(communityId, userEmail);
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.changeMemberRole(member, role));
  }
}
