package ru.senla.socialnetwork.controllers;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.friendRequests.RemoveFriendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.SendRequestDTO;
import ru.senla.socialnetwork.model.enums.FriendStatus;

public interface FriendRequestController {

  ResponseEntity<?> showFriends(String userEmail);

  ResponseEntity<?> showOutgoingRequests(String userEmail);

  ResponseEntity<?> showIncomingRequests(
      String recipientEmail, FriendStatus status, Principal principal);

  ResponseEntity<?> sendRequest(SendRequestDTO request);

  ResponseEntity<?> respondRequest(RespondRequestDTO request);

  ResponseEntity<?> removeFriend(RemoveFriendRequestDTO request, Principal principal);
}
