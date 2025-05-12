package ru.senla.socialnetwork.facades.friendRequests;

import java.util.List;
import ru.senla.socialnetwork.dto.friendRequests.FriendRequestDTO;
import ru.senla.socialnetwork.dto.friendRequests.RespondRequestDTO;
import ru.senla.socialnetwork.dto.users.UserResponseDTO;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public interface FriendRequestFacade {
  List<FriendRequestDTO> getAllByUser(String userEmail);

  List<UserResponseDTO> getFriendsByUser(String userEmail);

  List<FriendRequestDTO> getIncomingRequests(String userEmail, FriendStatus status);

  List<FriendRequestDTO> getOutgoingRequests(String userEmail);

  FriendRequestDTO send(String senderEmail, String recipientEmail);

  FriendRequestDTO cancel(String senderEmail, String recipientEmail);

  FriendRequestDTO respond(RespondRequestDTO requestDTO, String recipientEmail);

  FriendRequestDTO unfriend(String userEmail, String unfriendEmail);
}
