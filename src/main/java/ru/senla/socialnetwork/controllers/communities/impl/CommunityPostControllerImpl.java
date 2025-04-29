package ru.senla.socialnetwork.controllers.communities.impl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.communities.CommunityPostController;
import ru.senla.socialnetwork.dto.communitites.CommunityDTO;
import ru.senla.socialnetwork.dto.communitites.CreateCommunityDTO;
import ru.senla.socialnetwork.services.communities.CommunityPostService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/communities/{communityId}/posts")
public class CommunityPostControllerImpl implements CommunityPostController {
  private final CommunityPostService communityPostService;


}
