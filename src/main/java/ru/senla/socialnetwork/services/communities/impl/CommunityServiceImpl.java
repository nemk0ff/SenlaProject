package ru.senla.socialnetwork.services.communities.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.communities.CommunityDao;
import ru.senla.socialnetwork.services.communities.CommunityService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CommunityServiceImpl implements CommunityService {
  private final CommunityDao communityDao;

}
