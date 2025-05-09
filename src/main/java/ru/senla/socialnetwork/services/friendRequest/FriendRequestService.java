package ru.senla.socialnetwork.services.friendRequest;

import java.util.List;
import ru.senla.socialnetwork.model.friendRequests.FriendRequest;
import ru.senla.socialnetwork.model.users.User;
import ru.senla.socialnetwork.model.friendRequests.FriendStatus;

public interface FriendRequestService {
  List<FriendRequest> getAllByUser(Long userId);

  List<User> getFriendsByUser(Long userId);

  List<FriendRequest> getIncomingRequests(Long userId, FriendStatus status);

  List<FriendRequest> getOutgoingRequests(Long userId);

  FriendRequest send(User sender, User recipient);

  FriendRequest cancel(User sender, User recipient);

  FriendRequest replyToRequest(User sender, User recipient, FriendStatus status);

  FriendRequest unfriend(User user, User unfriend);

  boolean isFriends(Long firstId, Long secondId);
}
