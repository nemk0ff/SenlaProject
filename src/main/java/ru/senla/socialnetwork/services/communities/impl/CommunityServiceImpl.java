package ru.senla.socialnetwork.services.communities.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.senla.socialnetwork.dao.communities.CommunityDao;
import ru.senla.socialnetwork.model.communities.Community;
import ru.senla.socialnetwork.services.communities.CommunityService;

@Slf4j
@Service
@AllArgsConstructor
public class CommunityServiceImpl implements CommunityService {
  private final CommunityDao communityDao;

  @Override
  public Community get(Long communityId) {
    return communityDao.find(communityId).orElseThrow(
        () -> new EntityNotFoundException("Сообщество не найдено"));
  }

  @Override
  public List<Community> getAll() {
    return communityDao.getAll();
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
