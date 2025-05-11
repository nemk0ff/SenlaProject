package ru.senla.socialnetwork.facades.communities.impl;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;
import ru.senla.socialnetwork.dto.mappers.CommunityMapper;
import ru.senla.socialnetwork.exceptions.communities.CommunityException;
import ru.senla.socialnetwork.facades.communities.CommunityFacade;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityMember;
import ru.senla.socialnetwork.model.MemberRole;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.users.UserRole;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;
import ru.senla.socialnetwork.services.communities.CommunityService;
import ru.senla.socialnetwork.services.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommunityFacadeImpl implements CommunityFacade {
  private final CommunityService communityService;
  private final CommunityMemberService communityMemberService;
  private final UserService userService;

  @Override
  public CommunityDTO create(CreateCommunityDTO communityDTO, String clientEmail) {
    User owner = userService.getUserByEmail(clientEmail);

    Community community = Community.builder()
        .name(communityDTO.name())
        .description(communityDTO.description())
        .created_at(ZonedDateTime.now())
        .build();

    Community savedCommunity = communityService.save(community);

    CommunityMember ownerMember = communityMemberService.joinCommunity(savedCommunity, owner);
    communityMemberService.changeMemberRole(ownerMember, MemberRole.ADMIN);

    return CommunityMapper.INSTANCE.toDTO(savedCommunity);
  }

  @Override
  public void delete(Long communityId, String clientEmail) {
    User client = userService.getUserByEmail(clientEmail);

    if(!client.getRole().equals(UserRole.ADMIN)) {
      if(communityMemberService.isMember(communityId, clientEmail)) {
        communityMemberService.checkIsAdmin(communityId, clientEmail);
      } else {
        throw new CommunityException("Недостаточно прав для выполнения этой операции");
      }
    }

    List<CommunityMember> members = communityMemberService.getAll(communityId);
    members.forEach(communityMemberService::delete);
    Community community = communityService.get(communityId);
    communityService.delete(community);
  }

  @Override
  @Transactional(readOnly = true)
  public CommunityDTO get(Long communityId) {
    return CommunityMapper.INSTANCE.toDTO(communityService.get(communityId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CommunityDTO> getAll() {
    List<Community> communities = communityService.getAll();
    return CommunityMapper.INSTANCE.toListDTO(communities);
  }

  @Override
  public CommunityDTO change(ChangeCommunityDTO communityDTO, String clientEmail) {
    communityMemberService.checkIsAdmin(communityDTO.id(), clientEmail);

    Community community = communityService.get(communityDTO.id());

    community.setName(communityDTO.name());
    community.setDescription(communityDTO.description());

    Community updatedCommunity = communityService.save(community);
    return CommunityMapper.INSTANCE.toDTO(updatedCommunity);
  }
}
