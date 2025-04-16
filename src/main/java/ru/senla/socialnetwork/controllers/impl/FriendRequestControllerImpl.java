package ru.senla.socialnetwork.controllers.impl;

import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.FriendRequestController;
import ru.senla.socialnetwork.model.enums.FriendStatus;
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
    return ResponseEntity.ok(friendRequestService.getFriendsByUser(principal.getName()));
  }

  @Override
  @PostMapping("/requests/outgoing")
  public ResponseEntity<?> showOutgoingRequests(Principal principal) {
    return ResponseEntity.ok(friendRequestService.getOutgoingRequests(principal.getName()));
  }

  @Override
  @PostMapping("/requests/outgoing")
  public ResponseEntity<?> showIncomingRequests(@RequestParam("status") FriendStatus status,
                                                Principal principal) {
    return ResponseEntity.ok(friendRequestService.getIncomingRequests(principal.getName(), status));
  }

  @Override
  @PostMapping("/requests/send")
  public ResponseEntity<?> sendRequest(@RequestParam("recipient") String recipientEmail,
                                       Principal principal) {
    return ResponseEntity.ok(friendRequestService.sendRequest(principal.getName(), recipientEmail));
  }

  @Override
  @PostMapping("/requests/reply")
  public ResponseEntity<?> replyToRequest(@RequestParam("sender") String senderEmail,
                                          @RequestParam("status") FriendStatus status,
                                          Principal principal) {
    return ResponseEntity.ok(friendRequestService
        .replyToRequest(senderEmail, principal.getName(), status));
  }

//  @Override
//  @GetMapping("/requests")
//  public ResponseEntity<?> getAll() {
//    return ResponseEntity.ok(friendRequestService.getAll());
//  }
}
