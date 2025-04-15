package ru.senla.socialnetwork.controllers.impl;

import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.FriendRequestController;
import ru.senla.socialnetwork.services.FriendRequestService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/friends")
public class FriendRequestControllerImpl implements FriendRequestController {
  private final FriendRequestService friendRequestService;

  @Override
  @GetMapping()
  public ResponseEntity<?> showFriends(Principal principal) {
    return ResponseEntity.ok(friendRequestService.getByUserEmail(principal.getName()));
  }

  @Override
  @GetMapping("/requests")
  public ResponseEntity<?> getAll() {
    return ResponseEntity.ok(friendRequestService.getAll());
  }
}
