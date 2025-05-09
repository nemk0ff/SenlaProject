package ru.senla.socialnetwork.services.communities.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.communities.CommunityMemberDao;
import ru.senla.socialnetwork.exceptions.communities.CommunityMemberException;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;

@Slf4j
@Service
@AllArgsConstructor
public class CommunityMemberServiceImpl implements CommunityMemberService {
  private final CommunityMemberDao communityMemberDao;

  @Override
  public CommunityMember get(Long communityId, String userEmail) {
    return communityMemberDao.findByCommunityAndUser(communityId, userEmail).
        orElseThrow(() -> new EntityNotFoundException("Участник сообщества не найден"));
  }

  @Override
  public List<CommunityMember> getAll(Long communityId) {
    return communityMemberDao.findAllByCommunity(communityId);
  }

  @Override
  public CommunityMember joinCommunity(Community community, User user) {
    log.info("Создание участника сообщества: id={}, email={}", community.getId(), user.getEmail());
    CommunityMember member = CommunityMember.builder()
        .community(community)
        .user(user)
        .joinDate(ZonedDateTime.now())
        .role(MemberRole.MEMBER)
        .isBanned(false)
        .build();
    log.info("Участник сообщества создан: {}", member);
    return communityMemberDao.saveOrUpdate(member);
  }

  @Override
  public void leaveCommunity(CommunityMember member) {
    communityMemberDao.delete(member);
  }

  @Override
  public CommunityMember banMember(CommunityMember member, String reason) {
    member.setIsBanned(true);
    member.setBannedReason(reason);
    return communityMemberDao.saveOrUpdate(member);
  }

  @Override
  public CommunityMember unbanMember(CommunityMember member) {
    member.setIsBanned(false);
    member.setBannedReason(null);
    return communityMemberDao.saveOrUpdate(member);
  }

  @Override
  public CommunityMember changeMemberRole(CommunityMember member, MemberRole role) {
    member.setRole(role);
    return communityMemberDao.saveOrUpdate(member);
  }

  @Override
  public boolean isMember(Long communityId, String userEmail) {
    return communityMemberDao.findByCommunityAndUser(communityId, userEmail).isPresent();
  }

  @Override
  public void checkIsBanned(Long communityId, String userEmail) {
    log.info("Проверка, не забанен ли пользователь {} в сообществе {}...", userEmail, communityId);
    if (get(communityId, userEmail).getIsBanned()) {
      throw new CommunityMemberException("Вы забанены в этом сообществе");
    }
    log.info("Пользователь {} не забанен в сообществе {}, проверка пройдена.", userEmail,
        communityId);
  }

  @Override
  public void checkIsAdminOrModer(Long communityId, String userEmail) {
    log.info("Проверка, имеет ли пользователь {} права админа или модератора в сообществе {}...",
        userEmail, communityId);
    if(hasRight(communityId, userEmail, MemberRole.MEMBER)) {
      throw new CommunityMemberException("У вас недостаточно прав для выполнения этой операции");
    }
    log.info("Пользователь {} имеет права админа или модератора в сообществе {}, проверка " +
            "пройдена.", userEmail, communityId);
  }

  @Override
  public void checkIsAdmin(Long communityId, String userEmail) {
    log.info("Проверка, имеет ли пользователь {} права админа в сообществе {}...",
        userEmail, communityId);
    if(!hasRight(communityId, userEmail, MemberRole.ADMIN)) {
      throw new CommunityMemberException("У вас недостаточно прав для выполнения этой операции");
    }
    log.info("Пользователь {} имеет права админа в сообществе {}, проверка " +
        "пройдена.", userEmail, communityId);
  }

  private boolean hasRight(Long communityId, String userEmail, MemberRole role) {
    return get(communityId, userEmail).getRole().equals(role);
  }
}
