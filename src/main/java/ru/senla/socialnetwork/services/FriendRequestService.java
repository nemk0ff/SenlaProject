package ru.senla.socialnetwork.services;

import java.util.List;
import ru.senla.socialnetwork.model.entities.friendRequests.FriendRequest;
import ru.senla.socialnetwork.model.entities.users.User;
import ru.senla.socialnetwork.model.enums.FriendStatus;

public interface FriendRequestService {
  List<FriendRequest> getAllByUser(String userEmail);

  List<User> getFriendsByUser(String userEmail);

  List<FriendRequest> getIncomingRequests(String userEmail, FriendStatus status);

  List<FriendRequest> getOutgoingRequests(String userEmail);

  FriendRequest sendRequest(String senderEmail, String recipientEmail);

  FriendRequest replyToRequest(String senderEmail, String recipientEmail, FriendStatus status);

  void unfriend(String userEmail, String unfriendEmail);
}
