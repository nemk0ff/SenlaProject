package ru.senla.socialnetwork.services.communities.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.communities.CommunityDao;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;
import ru.senla.socialnetwork.dto.mappers.CommunityMapper;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.model.communities.CommunityType;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.services.chats.CommonChatService;
import ru.senla.socialnetwork.services.common.CommonService;
import ru.senla.socialnetwork.services.communities.CommunityService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CommunityServiceImpl implements CommunityService {
  private final CommunityDao communityDao;

  @Override
  public Community get(Long communityId) {
    return communityDao.find(communityId).orElseThrow(
        () -> new EntityNotFoundException("Сообщество не найдено"));
  }

  @Override
  public Community save(Community communityToUpdate) {
    return communityDao.saveOrUpdate(communityToUpdate);
  }

  @Override
  public void delete(Community communityToUpdate) {
    communityDao.delete(communityToUpdate);
  }
}
