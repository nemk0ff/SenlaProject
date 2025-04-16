package ru.senla.socialnetwork.controllers;

import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.FriendRequestDTO;
import ru.senla.socialnetwork.model.enums.FriendStatus;

public interface FriendRequestController {

  ResponseEntity<?> showFriends(String userEmail);

  ResponseEntity<?> showOutgoingRequests(String userEmail);

  ResponseEntity<?> showIncomingRequests(String recipientEmail,
                                         @NotNull FriendStatus status,
                                         Principal principal);

  ResponseEntity<?> sendRequest(FriendRequestDTO friendRequestDTO);

  ResponseEntity<?> replyToRequest(FriendRequestDTO friendRequestDTO);

  ResponseEntity<?> unfriend(String friendToRemoveEmail, String userEmail, Principal principal);
}
