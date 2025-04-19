package ru.senla.socialnetwork.repository;

import java.util.List;
import java.util.Optional;
import ru.senla.socialnetwork.model.entities.FriendRequest;
import ru.senla.socialnetwork.model.entities.User;

public interface FriendRequestDao {
  List<User> findFriendsByUserId(Long userId);

  List<FriendRequest> getAllByUserId(Long userId);

  Optional<FriendRequest> getByUsersIds(Long firstUser, Long secondUser, boolean isOrderRelevant);

  FriendRequest add(FriendRequest friendRequest);
}
