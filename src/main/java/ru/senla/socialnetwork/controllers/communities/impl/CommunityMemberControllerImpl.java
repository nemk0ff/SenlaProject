package ru.senla.socialnetwork.controllers.communities.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityMemberController;
import ru.senla.socialnetwork.services.communities.CommunityMemberService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities/{communityId}/members")
public class CommunityMemberControllerImpl implements CommunityMemberController {
  private final CommunityMemberService communityMemberService;

}
