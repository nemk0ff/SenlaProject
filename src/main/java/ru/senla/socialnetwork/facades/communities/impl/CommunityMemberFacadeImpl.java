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
  public List<CommunityMemberDTO> getAll(Long communityId) {
    return CommunityMemberMapper.INSTANCE.toListDTO(communityMemberService.getAll(communityId));
  }

  @Override
  @Transactional
  public CommunityMemberDTO joinCommunity(Long communityId, String userEmail) {
    log.info("(Facade) создание участника сообщества: id={}, email={}", communityId, userEmail);
    User user = userService.getUserByEmail(userEmail);
    Community community = communityService.get(communityId);
    log.info("Найдены user={} и community={}", user.getId(), community.getId());

    if(communityMemberService.isMember(communityId, user.getId())) {
      throw new CommunityMemberException(userEmail + " уже является участником сообщества");
    }
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.joinCommunity(community, user));
  }

  @Override
  @Transactional
  public void leaveCommunity(Long communityId, String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    communityMemberService.leaveCommunity(member);
  }

  @Override
  @Transactional
  public CommunityMemberDTO banMember(Long communityId, String userEmail, String reason) {
    User user = userService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.banMember(member, reason));
  }

  @Override
  @Transactional
  public CommunityMemberDTO unbanMember(Long communityId, String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.unbanMember(member));
  }

  @Override
  @Transactional
  public CommunityMemberDTO changeMemberRole(Long communityId, String userEmail, MemberRole role) {
    User user = userService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    return CommunityMemberMapper.INSTANCE
        .toDTO(communityMemberService.changeMemberRole(member, role));
  }

  @Override
  @Transactional
  public boolean isBanned(Long communityId, String userEmail) {
    User user = userService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    return member.getIsBanned();
  }

  @Override
  @Transactional
  public boolean isAdmin(Long communityId, String userEmail) {
    return hasRight(communityId, userEmail, MemberRole.ADMIN);
  }

  @Override
  @Transactional
  public boolean isModerator(Long communityId, String userEmail) {
    return hasRight(communityId, userEmail, MemberRole.MODERATOR);
  }

  private boolean hasRight(Long communityId, String userEmail, MemberRole role) {
    User user = userService.getUserByEmail(userEmail);
    CommunityMember member = communityMemberService.get(communityId, user.getId());
    return member.getRole().equals(role);
  }
}
