package ru.senla.socialnetwork.controllers;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.model.enums.FriendStatus;

public interface FriendRequestController {

  ResponseEntity<?> showFriends(Principal principal);

  ResponseEntity<?> showOutgoingRequests(Principal principal);

  ResponseEntity<?> showIncomingRequests(FriendStatus status, Principal principal);

  ResponseEntity<?> sendRequest(String recipientEmail, Principal principal);

  ResponseEntity<?> replyToRequest(String senderEmail, FriendStatus status, Principal principal);
}
