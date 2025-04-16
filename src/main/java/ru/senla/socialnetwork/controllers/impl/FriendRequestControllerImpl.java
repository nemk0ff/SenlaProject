package ru.senla.socialnetwork.controllers.impl;

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
import ru.senla.socialnetwork.dto.FriendRequestDTO;
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
  @GetMapping()
  public ResponseEntity<?> showFriends(String userEmail) {
    return ResponseEntity.ok(friendRequestService.getFriendsByUser(userEmail));
  }

  @Override
  @GetMapping("/out_requests")
  @PreAuthorize("hasRole('ADMIN') or #userEmail == authentication.name")
  public ResponseEntity<?> showOutgoingRequests(String userEmail) {
    return ResponseEntity.ok(FriendRequestMapper.INSTANCE
        .toListDTO(friendRequestService.getOutgoingRequests(userEmail)));
  }

  @Override
  @GetMapping("/requests")
  public ResponseEntity<?> showIncomingRequests(
      @RequestParam(required = false) String recipientEmail,
      @RequestParam @NotNull FriendStatus status,
      Principal principal) {
    String actual = recipientEmail != null ? recipientEmail : principal.getName();
    return ResponseEntity.ok(FriendRequestMapper.INSTANCE
        .toListDTO(friendRequestService.getIncomingRequests(actual, status)));
  }

  @Override
  @PostMapping("/send")
  @PreAuthorize("hasRole('ADMIN') or #friendRequestDTO.senderEmail == authentication.name")
  public ResponseEntity<?> sendRequest(@RequestBody FriendRequestDTO friendRequestDTO) {
    return ResponseEntity.ok(FriendRequestMapper.INSTANCE
        .toDto(friendRequestService.sendRequest(friendRequestDTO.getSenderEmail(),
            friendRequestDTO.getRecipientEmail())));
  }

  @Override
  @PostMapping("/reply")
  @PreAuthorize("hasRole('ADMIN') or #friendRequestDTO.recipientEmail == authentication.name")
  public ResponseEntity<?> replyToRequest(@RequestBody FriendRequestDTO friendRequestDTO) {
    return ResponseEntity.ok(FriendRequestMapper.INSTANCE
        .toDto(friendRequestService.replyToRequest(friendRequestDTO.getSenderEmail(),
            friendRequestDTO.getRecipientEmail(), friendRequestDTO.getStatus())));
  }

  @Override
  @PostMapping("/unfriend")
  @PreAuthorize("hasRole('ADMIN') or #friendRequestDTO.userEmail == authentication.name")
  public ResponseEntity<?> unfriend(
      @RequestParam("unfriendEmail") String friendToRemoveEmail,
      @RequestParam(name = "userEmail", required = false) String userEmail,
      Principal principal) {
    String actualUserEmail = userEmail != null ? userEmail : principal.getName();
    friendRequestService.unfriend(actualUserEmail, friendToRemoveEmail);
    return ResponseEntity.ok(friendToRemoveEmail + " удалён из списка друзей "
        + actualUserEmail);
  }
}
