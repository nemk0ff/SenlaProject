package ru.senla.socialnetwork.facade.communities.impl;

import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dto.communitites.ChangeCommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;
import ru.senla.socialnetwork.dto.mappers.CommunityMapper;
import ru.senla.socialnetwork.facade.communities.CommunityFacade;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityType;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.common.CommonService;
import ru.senla.socialnetwork.services.communities.CommunityService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityFacadeImpl implements CommunityFacade {
  private final CommunityService communityService;
  private final CommonService commonService;

  @Override
  public CommunityDTO create(CreateCommunityDTO communityDTO) {
    log.debug("Создание нового сообщества #{}", communityDTO);

    User owner = commonService.getUserByEmail(communityDTO.owner());

    Community community = Community.builder()
        .owner(owner)
        .name(communityDTO.name())
        .description(communityDTO.description())
        .type(communityDTO.type() == null ?
            CommunityType.OPEN : communityDTO.type())
        .created_at(ZonedDateTime.now())
        .build();

    Community savedCommunity = communityService.save(community);
    return CommunityMapper.INSTANCE.toDTO(savedCommunity);
  }

  @Override
  public void delete(Long communityId) {
    log.debug("Удаление сообщества #{}", communityId);

    Community community = communityService.get(communityId);
    communityService.delete(community);
  }

  @Override
  public CommunityDTO get(Long communityId) {
    log.debug("Получение сообщества #{}", communityId);

    return CommunityMapper.INSTANCE.toDTO(communityService.get(communityId));
  }

  @Override
  public CommunityDTO change(ChangeCommunityDTO communityDTO) {
    Community community = communityService.get(communityDTO.id());

    community.setName(communityDTO.name());
    community.setDescription(communityDTO.description());
    community.setType(communityDTO.type());

    Community updatedCommunity = communityService.save(community);
    return CommunityMapper.INSTANCE.toDTO(updatedCommunity);
  }
}
