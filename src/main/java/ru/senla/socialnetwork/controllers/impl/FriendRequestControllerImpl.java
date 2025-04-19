package ru.senla.socialnetwork.controllers.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.socialnetwork.controllers.FriendRequestController;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.SendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RemoveFriendRequestDTO;
import ru.senla.socialnetwork.dto.mappers.FriendRequestMapper;
import ru.senla.socialnetwork.model.enums.FriendStatus;
import ru.senla.socialnetwork.services.FriendRequestService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/friends")
public class FriendRequestControllerImpl implements FriendRequestController {
  private final FriendRequestService friendRequestService;

  @Override
  @GetMapping
  public ResponseEntity<?> showFriends(String userEmail) {
    return ResponseEntity.ok(friendRequestService.getFriendsByUser(userEmail));
  }

  @Override
  @GetMapping("/outgoing_requests")
  @PreAuthorize("hasRole('ADMIN') or #userEmail == authentication.name")
  public ResponseEntity<?> showOutgoingRequests(String userEmail) {
    return ResponseEntity.ok(FriendRequestMapper.INSTANCE
        .toListDTO(friendRequestService.getOutgoingRequests(userEmail)));
  }

  @Override
  @GetMapping("/incoming_requests")
  public ResponseEntity<?> showIncomingRequests(
      @RequestParam(required = false) @Email String recipientEmail,
      @RequestParam @NotNull FriendStatus status,
      Principal principal) {
    String actual = recipientEmail != null ? recipientEmail : principal.getName();
    return ResponseEntity.ok(FriendRequestMapper.INSTANCE
        .toListDTO(friendRequestService.getIncomingRequests(actual, status)));
  }

  @Override
  @PostMapping("/send")
  @PreAuthorize("hasRole('ADMIN') or #request.senderEmail == authentication.name")
  public ResponseEntity<?> sendRequest(@Valid @RequestBody SendRequestDTO request) {
    return ResponseEntity.ok(FriendRequestMapper.INSTANCE
        .toDto(friendRequestService.sendRequest(request.senderEmail(),
            request.recipientEmail())));
  }

  @Override
  @PostMapping("/respond")
  @PreAuthorize("hasRole('ADMIN') or #request.recipientEmail == authentication.name")
  public ResponseEntity<?> respondRequest(@Valid @RequestBody RespondRequestDTO request) {
    return ResponseEntity.ok(FriendRequestMapper.INSTANCE
        .toDto(friendRequestService.replyToRequest(request.senderEmail(),
            request.recipientEmail(), request.status())));
  }

  @Override
  @PostMapping("/remove")
  @PreAuthorize("hasRole('ADMIN') or #request.userEmail == authentication.name")
  public ResponseEntity<?> removeFriend(
      @RequestBody @Valid RemoveFriendRequestDTO request,
      Principal principal) {
    String actualUserEmail = request.userEmail() != null ? request.userEmail() : principal.getName();
    friendRequestService.unfriend(actualUserEmail, request.friendEmail());
    return ResponseEntity.ok(request.friendEmail() + " удалён из списка друзей "
        + actualUserEmail);
  }
}
