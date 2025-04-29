package ru.senla.socialnetwork.controllers.communities.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityController;
import ru.senla.socialnetwork.services.communities.CommunityService;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities")
public class CommunityControllerImpl implements CommunityController {
  private final CommunityService communityService;

}
