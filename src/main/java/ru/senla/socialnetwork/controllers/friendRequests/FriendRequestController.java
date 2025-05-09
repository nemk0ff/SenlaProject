package ru.senla.socialnetwork.controllers.friendRequests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public interface FriendRequestController {

  ResponseEntity<?> showAllByUser(String userEmail);

  ResponseEntity<?> showFriends(String userEmail);

  ResponseEntity<?> showOutgoingRequests(Authentication auth);

  ResponseEntity<?> showIncomingRequests(@NotNull FriendStatus status, Authentication auth);

  ResponseEntity<?> sendRequest(String recipient, Authentication auth);

  ResponseEntity<?> respondRequest(@Valid RespondRequestDTO request, Authentication auth);

  ResponseEntity<?> removeFriend(String recipient, Authentication auth);
}
