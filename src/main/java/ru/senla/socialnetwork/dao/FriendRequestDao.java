package ru.senla.socialnetwork.dao;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.model.chats.ChatMessage;
import ru.senla.socialnetwork.model.friendRequests.FriendRequest;
import ru.senla.socialnetwork.model.users.User;

public interface FriendRequestDao extends GenericDao<FriendRequest> {
  List<User> findFriendsByUserId(Long userId);

  List<FriendRequest> getAllByUserId(Long userId);

  Optional<FriendRequest> getByUsersIds(Long firstUser, Long secondUser, boolean isOrderRelevant);
}
