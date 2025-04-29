package ru.senla.socialnetwork.services.communities.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.socialnetwork.dao.communities.CommunityMemberDao;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CommunityMemberServiceImpl implements CommunityMemberService {
  private final CommunityMemberDao communityMemberDao;

}
