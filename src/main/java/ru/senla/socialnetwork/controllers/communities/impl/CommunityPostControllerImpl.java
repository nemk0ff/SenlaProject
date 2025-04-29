package ru.senla.socialnetwork.controllers.communities.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityPostController;
import ru.senla.socialnetwork.services.communities.CommunityPostService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities/{communityId}/posts")
public class CommunityPostControllerImpl implements CommunityPostController {
  private final CommunityPostService communityPostService;
}
