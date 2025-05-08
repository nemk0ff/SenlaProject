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
import ru.senla.socialnetwork.facades.communities.CommunityFacade;
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
public class CommunityFacadeImpl implements CommunityFacade {
  private final CommunityService communityService;
  private final CommunityMemberService communityMemberService;
  private final UserService userService;

  @Override
  @Transactional
  public CommunityDTO create(CreateCommunityDTO communityDTO) {
    log.debug("Создание нового сообщества #{}", communityDTO);

    User owner = userService.getUserByEmail(communityDTO.owner());

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
  @Transactional
  public void delete(Long communityId) {
    log.debug("Удаление сообщества #{}", communityId);

    Community community = communityService.get(communityId);
    communityService.delete(community);
  }

  @Override
  @Transactional(readOnly = true)
  public CommunityDTO get(Long communityId) {
    log.debug("Получение сообщества #{}", communityId);

    return CommunityMapper.INSTANCE.toDTO(communityService.get(communityId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CommunityDTO> getAll() {
    List<Community> communities = communityService.getAll();
    return CommunityMapper.INSTANCE.toListDTO(communities);
  }

  @Override
  @Transactional
  public CommunityDTO change(ChangeCommunityDTO communityDTO) {
    Community community = communityService.get(communityDTO.id());

    community.setName(communityDTO.name());
    community.setDescription(communityDTO.description());

    Community updatedCommunity = communityService.save(community);
    return CommunityMapper.INSTANCE.toDTO(updatedCommunity);
  }
}
