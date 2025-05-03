package ru.senla.socialnetwork.controllers.friendRequests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import ru.senla.socialnetwork.dto.friendRequests.RemoveFriendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.SendRequestDTO;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public interface FriendRequestController {

  ResponseEntity<?> showFriends(String userEmail);

  ResponseEntity<?> showOutgoingRequests(String userEmail);

  ResponseEntity<?> showIncomingRequests(@Email String recipientEmail,
                                         @NotNull FriendStatus status);

  ResponseEntity<?> sendRequest(@Valid SendRequestDTO request);

  ResponseEntity<?> respondRequest(@Valid RespondRequestDTO request);

  ResponseEntity<?> removeFriend(@Valid RemoveFriendRequestDTO request);
}
