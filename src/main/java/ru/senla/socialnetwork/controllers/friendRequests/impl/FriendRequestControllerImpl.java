package ru.senla.socialnetwork.controllers.friendRequests.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.friendRequests.FriendRequestController;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.SendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RemoveFriendRequestDTO;
import ru.senla.socialnetwork.facades.friendRequests.FriendRequestFacade;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/friends")
public class FriendRequestControllerImpl implements FriendRequestController {
  private final FriendRequestFacade friendRequestFacade;

  @Override
  @GetMapping
  public ResponseEntity<?> showFriends(String userEmail) {
    return ResponseEntity.ok(friendRequestFacade.getFriendsByUser(userEmail));
  }

  @Override
  @GetMapping("/outgoing_requests")
  @PreAuthorize("hasRole('ADMIN') or #userEmail == authentication.name")
  public ResponseEntity<?> showOutgoingRequests(String userEmail) {
    return ResponseEntity.ok(friendRequestFacade.getOutgoingRequests(userEmail));
  }

  @Override
  @GetMapping("/incoming_requests")
  @PreAuthorize("hasRole('ADMIN') or #recipientEmail == authentication.name")
  public ResponseEntity<?> showIncomingRequests(
      @RequestParam @Email String recipientEmail,
      @RequestParam @NotNull FriendStatus status) {
    return ResponseEntity.ok(friendRequestFacade.getIncomingRequests(recipientEmail, status));
  }

  @Override
  @PostMapping("/send")
  @PreAuthorize("hasRole('ADMIN') or #request.senderEmail == authentication.name")
  public ResponseEntity<?> sendRequest(@RequestBody @Valid SendRequestDTO request) {
    return ResponseEntity.ok(friendRequestFacade.sendRequest(request.senderEmail(),
        request.recipientEmail()));
  }

  @Override
  @PostMapping("/respond")
  @PreAuthorize("hasRole('ADMIN') or #request.recipientEmail == authentication.name")
  public ResponseEntity<?> respondRequest(@RequestBody @Valid RespondRequestDTO request) {
    return ResponseEntity.ok(friendRequestFacade.replyToRequest(request.senderEmail(),
            request.recipientEmail(), request.status()));
  }

  @Override
  @DeleteMapping("/remove")
  @PreAuthorize("hasRole('ADMIN') or #request.userEmail == authentication.name")
  public ResponseEntity<?> removeFriend(
      @RequestBody @Valid RemoveFriendRequestDTO request) {
    friendRequestFacade.unfriend(request.userEmail(), request.friendEmail());
    return ResponseEntity.ok(request.friendEmail() + " удалён из списка друзей "
        + request.userEmail());
  }
}
