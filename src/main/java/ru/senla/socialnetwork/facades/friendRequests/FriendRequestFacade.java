package ru.senla.socialnetwork.facades.friendRequests;

import java.util.List;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.users.UserDTO;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public interface FriendRequestFacade {
  List<FriendRequestDTO> getAllByUser(String userEmail);

  List<UserDTO> getFriendsByUser(String userEmail);

  List<FriendRequestDTO> getIncomingRequests(String userEmail, FriendStatus status);

  List<FriendRequestDTO> getOutgoingRequests(String userEmail);

  FriendRequestDTO send(String senderEmail, String recipientEmail);

  FriendRequestDTO respond(RespondRequestDTO requestDTO, String recipientEmail);

  void unfriend(String userEmail, String unfriendEmail);
}
