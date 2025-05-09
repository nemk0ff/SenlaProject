package ru.senla.socialnetwork.controllers.friendRequests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public interface FriendRequestController {

  ResponseEntity<?> showAllByUser(@Email String userEmail);

  ResponseEntity<?> showFriends(@Email String userEmail);

  ResponseEntity<?> showOutgoingRequests(Authentication auth);

  ResponseEntity<?> showIncomingRequests(FriendStatus status, Authentication auth);

  ResponseEntity<?> sendRequest(@Email String recipient, Authentication auth);

  ResponseEntity<?> cancelRequest(@Email String recipient, Authentication auth);

  ResponseEntity<?> respondRequest(@Valid RespondRequestDTO request, Authentication auth);

  ResponseEntity<?> removeFriend(@Email String recipient, Authentication auth);
}
