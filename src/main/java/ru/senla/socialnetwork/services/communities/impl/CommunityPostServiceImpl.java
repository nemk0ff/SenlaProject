package ru.senla.socialnetwork.services.communities.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.communities.CommunityPostDao;
import ru.senla.socialnetwork.services.communities.CommunityPostService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {
  private final CommunityPostDao communityPostDao;

}
