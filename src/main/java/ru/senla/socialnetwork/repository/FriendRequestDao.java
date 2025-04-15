package ru.senla.socialnetwork.repository;

import java.util.Optional;
import ru.senla.socialnetwork.model.entities.FriendRequest;

public interface FriendRequestDao {
  Optional<FriendRequest> getByUserId(Long userId);
}
