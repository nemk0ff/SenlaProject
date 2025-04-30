package ru.senla.socialnetwork.services.communities.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.communities.CommunityMemberDao;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.general.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CommunityMemberServiceImpl implements CommunityMemberService {
  private final CommunityMemberDao communityMemberDao;

  @Override
  public CommunityMember get(Long communityId, Long userId) {
    return communityMemberDao.findByCommunityAndUser(communityId, userId).
        orElseThrow(() -> new EntityNotFoundException("Участник сообщества не найден"));
  }

  @Override
  public List<CommunityMember> getAll(Long communityId) {
    return communityMemberDao.findAllByCommunity(communityId);
  }

  @Override
  public CommunityMember joinCommunity(Community community, User user) {
    CommunityMember member = CommunityMember.builder()
        .community(community)
        .user(user)
        .joinDate(ZonedDateTime.now())
        .role(MemberRole.MEMBER)
        .build();
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
  public CommunityMember changeMemberRole(CommunityMember member, MemberRole role) {
    member.setRole(role);
    return communityMemberDao.saveOrUpdate(member);
  }

  @Override
  public boolean isMember(Long communityId, Long userId) {
    return communityMemberDao.findByCommunityAndUser(communityId, userId)
        .map(member -> !member.getIsBanned() && member.getRole() != MemberRole.MEMBER)
        .orElse(false);
  }
}
